package uk.co.saiman.instrument.acquisition.adq.impl;

import static uk.co.saiman.instrument.acquisition.adq.ProductFamily.V5;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import uk.co.saiman.instrument.acquisition.adq.impl.AdqDeviceManager.AdqLib;

public class TriggeredStreamAcquisition implements AdqAcquisition {
  private final Adq114DeviceImpl device;
  private final AdqLib lib;
  private final Pointer controlUnit;
  private final int deviceNumber;

  private final int samplesPerRecord;
  private final int numberOfRecords;
  private final int numberOfChannels;
  private final int numberOfBuffers;

  public TriggeredStreamAcquisition(
      Adq114DeviceImpl device,
      AdqLib lib,
      Pointer controlUnit,
      int deviceNumber) {
    this.device = device;
    this.lib = lib;
    this.controlUnit = controlUnit;
    this.deviceNumber = deviceNumber;

    this.samplesPerRecord = 128 * 10;
    this.numberOfRecords = 20;
    this.numberOfChannels = 1;
    this.numberOfBuffers = 16;

    // check(lib.ADQ_RunRecorderSelfTest(controlUnit, deviceNumber));

    lib.ADQ_DisarmTrigger(controlUnit, deviceNumber);

    lib.ADQ_SetTriggerMode(controlUnit, deviceNumber, device.getTriggerMode().toInt());
    check(
        lib.ADQ_SetTestPatternMode(controlUnit, deviceNumber, device.getTestPatternMode().toInt()));
    check(
        lib.ADQ_SetTestPatternConstant(controlUnit, deviceNumber, device.getTestPatternConstant()));

    int clockSource = 0;
    check(lib.ADQ_SetClockSource(controlUnit, deviceNumber, clockSource));
    int pllDivider = 2;
    lib.ADQ_SetPllFreqDivider(controlUnit, deviceNumber, pllDivider);

    lib.ADQ_SetTransferTimeout(controlUnit, deviceNumber, 5000);

    check(
        lib
            .ADQ_TriggeredStreamingSetupV5(
                controlUnit,
                deviceNumber,
                samplesPerRecord,
                0,
                0,
                0x0080 ^ // WFAVG_FLAG_ENABLE_AUTOARMNREAD
                    0x0010 // WFAVG_FLAG_READOUT_SLOW
            ));

    int bufferSize = lib.ADQ_GetTriggeredStreamingRecordSizeBytes(controlUnit, deviceNumber)
        + lib.ADQ_GetTriggeredStreamingHeaderSizeBytes(controlUnit, deviceNumber);
    System.out.println("Buffer size (bytes?) " + bufferSize);
    check(lib.ADQ_SetTransferBuffers(controlUnit, deviceNumber, numberOfBuffers, bufferSize));

    check(lib.ADQ_SetDataFormat(controlUnit, deviceNumber, device.getDataFormat().toInt()));

    try {
      Thread.sleep(20);
    } catch (InterruptedException e) {}

    check(lib.ADQ_SetStreamStatus(controlUnit, deviceNumber, 0x7));
    check(lib.ADQ_StartStreaming(controlUnit, deviceNumber));

    check(lib.ADQ_TriggeredStreamingArmV5(controlUnit, deviceNumber));
    // check(lib.ADQ_ArmTrigger(controlUnit, deviceNumber));

    // check(lib.ADQ_WaveformAveragingStartReadout(controlUnit, deviceNumber));
  }

  protected int getTsFlags() {
    int flags = 0;
    if (device.getProductFamily() == V5) {
      flags |= 0x0010; // WFAVG_FLAG_READOUT_SLOW; // Slow readout
    }

    /*-
    if (trig_mode == 2) {
    flags |= WFAVG_FLAG_COMPENSATE_EXT_TRIG;
    }
    */

    flags |= 0x0080; // WFAVG_FLAG_ENABLE_AUTOARMNREAD; // Enable the auto armnread feature

    // flags |= 0x0400; // double buffer mode

    return flags;
  }

  @Override
  public void close() {
    lib.ADQ_SetStreamStatus(controlUnit, deviceNumber, 0);
    lib.ADQ_DisarmTrigger(controlUnit, deviceNumber);
    lib.ADQ_TriggeredStreamingDisarmV5(controlUnit, deviceNumber);
    lib.ADQ_SetDataFormat(controlUnit, deviceNumber, 0);
  }

  @Override
  public void acquire() {
    int failureCounter = 0;
    int recordCounter = 0;
    int shutdownStatus = 0;

    Pointer headerTarget = new Memory(
        lib.ADQ_GetTriggeredStreamingHeaderSizeBytes(controlUnit, deviceNumber));
    Pointer[] dataTarget = new Pointer[AdqDeviceImpl.MAX_CHANNEL_COUNT];
    for (int i = 0; i < numberOfChannels; i++) {
      dataTarget[i] = new Memory(
          lib.ADQ_GetTriggeredStreamingRecordSizeBytes(controlUnit, deviceNumber));
    }

    do {
      // If trigger mode is software trigger ....
      // ...issue enough software triggers to
      // produce a single averaged record
      System.out.println("Record " + recordCounter);
      check(lib.ADQ_SWTrig(controlUnit, deviceNumber));

      boolean bufferReady = isReadyTriggeredStreamingGetStatus(lib, controlUnit, deviceNumber);

      System.out.println("    Last Error: " + lib.ADQ_GetLastError(controlUnit, deviceNumber));

      // Check for streaming overflow
      if (lib.ADQ_GetStreamOverflow(controlUnit, deviceNumber) != 0) {
        System.out.println("Warning: Streaming Overflow!");

        System.out.println("Initiated shutdown.");
        shutdownStatus = 1;
      }

      if (!bufferReady) {
        // No buffers filled yet, retry
        if (++failureCounter > 32) {
          throw new RuntimeException("No data");
        }
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {}
        continue;
      }
      failureCounter = 0;

      var noRecordsRead = new Memory(Integer.BYTES);
      lib
          .ADQ_GetTriggeredStreamingRecords(
              controlUnit,
              deviceNumber,
              1,
              dataTarget,
              headerTarget,
              noRecordsRead);
      int recordsRead = noRecordsRead.getInt(0);
      if (recordsRead > 0) {
        // Separate the raw streaming data of data_stream into the respective channels
        // in data_target
        System.out.println(" got one! ");

        recordCounter++;

      } else {
        System.out.println("ERROR: Failed to collect data from DMA buffer, shutting down.");
        lib.ADQ_WaveformAveragingShutdown(controlUnit, deviceNumber);
        shutdownStatus = 1;
      }

      // Check if the target number of records has been collected yet
      if (recordCounter == numberOfRecords) {
        System.out.println("Target number of records has been reached, shutting down WFA.");
        lib.ADQ_WaveformAveragingShutdown(controlUnit, deviceNumber);
        shutdownStatus = 1;
      }

      if (shutdownStatus == 1) {
        // Check if WFA is in idle state yet
        Pointer inIdle = new Memory(Byte.BYTES);
        lib.ADQ_WaveformAveragingGetStatus(controlUnit, deviceNumber, null, null, inIdle);
        if (inIdle.getByte(0) != 0) {
          System.out.println("Finalized shutdown.");
          shutdownStatus = 2;
        }
      }
    } while (shutdownStatus < 2);
  }

  private boolean isReadyTriggeredStreamingGetStatus(
      AdqLib lib,
      Pointer controlUnit,
      int deviceNumber) {
    Pointer ready = new Memory(Byte.BYTES);
    Pointer completed = new Memory(Integer.BYTES);
    Pointer idle = new Memory(Byte.BYTES);

    check(lib.ADQ_TriggeredStreamingGetStatusV5(controlUnit, deviceNumber, ready, completed, idle));

    System.out
        .println(
            "  Ready "
                + ready.getByte(0)
                + ", Completed "
                + completed.getInt(0)
                + ", Idle "
                + idle.getByte(0));

    return ready.getByte(0) >= 3;
  }

  protected void check(int result) {
    if (result == 0) {
      int lastError = lib.ADQ_GetLastError(controlUnit, deviceNumber);
      throw new IllegalStateException("Bad result: " + lastError);
    }
  }
}
