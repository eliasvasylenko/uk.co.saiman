/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.instrument.acquisition.adq.
 *
 * uk.co.saiman.instrument.acquisition.adq is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.acquisition.adq is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.acquisition.adq.impl;

import static uk.co.saiman.instrument.acquisition.adq.ProductFamily.V5;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import uk.co.saiman.instrument.acquisition.adq.Adq114DataFormat;
import uk.co.saiman.instrument.acquisition.adq.TestPatternMode;
import uk.co.saiman.instrument.acquisition.adq.impl.AdqDeviceManager.AdqLib;

public class WaveformAveragingStreamingAcquisition implements AdqAcquisition {
  private final Adq114DeviceImpl device;
  private final AdqLib lib;
  private final Pointer controlUnit;
  private final int deviceNumber;

  private final int samplesPerRecord;
  private final int numberOfWaveforms;
  private final int numberOfAverages;
  private final int numberOfChannels;
  private final int numberOfBuffers;

  public WaveformAveragingStreamingAcquisition(
      Adq114DeviceImpl device,
      AdqLib lib,
      Pointer controlUnit,
      int deviceNumber) {
    this.device = device;
    this.lib = lib;
    this.controlUnit = controlUnit;
    this.deviceNumber = deviceNumber;

    this.samplesPerRecord = 512 * 8;
    this.numberOfWaveforms = 4;
    this.numberOfAverages = 20;
    this.numberOfChannels = 1;
    this.numberOfBuffers = 16;

    // check(lib.ADQ_RunRecorderSelfTest(controlUnit, deviceNumber));

    lib.ADQ_DisarmTrigger(controlUnit, deviceNumber);

    lib.ADQ_SetTriggerMode(controlUnit, deviceNumber, device.getTriggerMode().toInt());
    check(
        lib
            .ADQ_SetTestPatternMode(
                controlUnit,
                deviceNumber,
                /* device.getTestPatternMode(). */TestPatternMode.NORMAL_OPERATION.toInt()));
    check(
        lib
            .ADQ_SetTestPatternConstant(
                controlUnit,
                deviceNumber,
                0/* device.getTestPatternConstant() */));

    int clockSource = 0;
    check(lib.ADQ_SetClockSource(controlUnit, deviceNumber, clockSource));
    int pllDivider = 2;
    lib.ADQ_SetPllFreqDivider(controlUnit, deviceNumber, pllDivider);

    lib.ADQ_SetTransferTimeout(controlUnit, deviceNumber, 5000);

    int bufferSize = numberOfChannels * samplesPerRecord * Integer.BYTES;
    System.out.println("Buffer size (bytes?) " + bufferSize);
    check(lib.ADQ_SetTransferBuffers(controlUnit, deviceNumber, numberOfBuffers, bufferSize));

    int flags = getWfaFlags();
    check(
        lib
            .ADQ_WaveformAveragingSetup(
                controlUnit,
                deviceNumber,
                numberOfWaveforms,
                samplesPerRecord,
                0,
                0,
                flags));
    System.out.println("WFA Flags " + flags);

    check(
        lib.ADQ_SetDataFormat(controlUnit, deviceNumber, Adq114DataFormat.UNPACKED_32BIT.toInt()));

    try {
      Thread.sleep(20);
    } catch (InterruptedException e) {}

    check(lib.ADQ_SetStreamStatus(controlUnit, deviceNumber, 0x7));
    check(lib.ADQ_StartStreaming(controlUnit, deviceNumber));

    check(lib.ADQ_WaveformAveragingArm(controlUnit, deviceNumber));
    // check(lib.ADQ_ArmTrigger(controlUnit, deviceNumber));

    // check(lib.ADQ_WaveformAveragingStartReadout(controlUnit, deviceNumber));
  }

  protected int getWfaFlags() {
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
    lib.ADQ_WaveformAveragingShutdown(controlUnit, deviceNumber);
    lib.ADQ_SetStreamStatus(controlUnit, deviceNumber, 0);
    lib.ADQ_DisarmTrigger(controlUnit, deviceNumber);
    lib.ADQ_WaveformAveragingDisarm(controlUnit, deviceNumber);
    lib.ADQ_SetDataFormat(controlUnit, deviceNumber, 0);
  }

  @Override
  public void acquire() {
    int failureCounter = 0;
    int recordCounter = 0;
    int shutdownStatus = 0;

    Pointer dataStream = new Memory(samplesPerRecord * numberOfChannels * Integer.BYTES);
    Pointer[] dataTarget = new Pointer[AdqDeviceImpl.MAX_CHANNEL_COUNT];
    for (int i = 0; i < numberOfChannels; i++) {
      dataTarget[i] = new Memory(samplesPerRecord * Integer.BYTES);
    }

    do {
      // If trigger mode is software trigger ....
      // ...issue enough software triggers to
      // produce a single averaged record
      System.out.println("Record " + recordCounter);
      for (int t = 0; t < numberOfWaveforms; t++) {
        check(lib.ADQ_SWTrig(controlUnit, deviceNumber));

        // isReadyWfaGetStatus(lib, controlUnit, deviceNumber);
      }

      // boolean bufferReady = isReadyWfaGetStatus(lib, controlUnit, deviceNumber);
      boolean bufferReady = isReadyTransferStatus(lib, controlUnit, deviceNumber);

      int lastError = lib.ADQ_GetLastError(controlUnit, deviceNumber);
      int streamingOverflow = lib.ADQ_GetStreamOverflow(controlUnit, deviceNumber);
      if (lastError != 0 || streamingOverflow != 0) {
        System.out.println("Warning: Last Error Code " + lastError);
        System.out.println("Warning: Streaming Overflow " + streamingOverflow);
        System.out.println("Initiated shutdown.");
        lib.ADQ_WaveformAveragingShutdown(controlUnit, deviceNumber);
        shutdownStatus = 1;

      } else if (!bufferReady) {
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

      int collect_result = lib.ADQ_CollectDataNextPage(controlUnit, deviceNumber);
      int resultError = lib.ADQ_GetLastError(controlUnit, deviceNumber);
      if (resultError != 0) {
        throw new RuntimeException("Error " + resultError);
      }

      if (collect_result != 0) {
        int bufferSize = samplesPerRecord * Integer.BYTES * numberOfChannels;
        var sourceBuffer = lib
            .ADQ_GetPtrStream(controlUnit, deviceNumber)
            .getByteBuffer(0, bufferSize);
        var sinkBuffer = dataStream.getByteBuffer(0, bufferSize);
        sinkBuffer.put(sourceBuffer);

        // Separate the raw streaming data of data_stream into the respective channels
        // in data_target
        lib
            .ADQ_WaveformAveragingParseDataStream(
                controlUnit,
                deviceNumber,
                samplesPerRecord,
                dataStream,
                dataTarget);

        recordCounter++;
      } else {
        System.out.println("ERROR: Failed to collect data from DMA buffer, shutting down.");
        lib.ADQ_WaveformAveragingShutdown(controlUnit, deviceNumber);
        shutdownStatus = 1;
      }

      // Check if the target number of records has been collected yet
      if (recordCounter == numberOfAverages) {
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

  private boolean isReadyTransferStatus(AdqLib lib, Pointer controlUnit, int deviceNumber) {
    var buffersFilledPointer = new Memory(Integer.BYTES);
    check(lib.ADQ_GetTransferBufferStatus(controlUnit, deviceNumber, buffersFilledPointer));
    final int buffersFilled = buffersFilledPointer.getInt(0);

    System.out.println(String.format("    Buffers currently filled: %3d", buffersFilled));

    return buffersFilled > 0;
  }

  private boolean isReadyWfaGetStatus(AdqLib lib, Pointer controlUnit, int deviceNumber) {
    Pointer ready = new Memory(Byte.BYTES);
    Pointer completed = new Memory(Integer.BYTES);
    Pointer idle = new Memory(Byte.BYTES);

    check(lib.ADQ_WaveformAveragingGetStatus(controlUnit, deviceNumber, ready, completed, idle));

    System.out
        .println(
            "    Ready "
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
