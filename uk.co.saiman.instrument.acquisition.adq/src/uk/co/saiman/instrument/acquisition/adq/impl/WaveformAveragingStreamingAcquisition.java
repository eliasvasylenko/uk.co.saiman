/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
import static uk.co.saiman.instrument.acquisition.adq.TriggerMode.SOFTWARE;
import static uk.co.saiman.log.Log.Level.INFO;
import static uk.co.saiman.measurement.Units.count;
import static uk.co.saiman.measurement.Units.second;

import java.util.function.Consumer;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import uk.co.saiman.data.function.ArraySampledContinuousFunction;
import uk.co.saiman.data.function.RegularSampledDomain;
import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.instrument.acquisition.AcquisitionException;
import uk.co.saiman.instrument.acquisition.adq.Adq114DataFormat;
import uk.co.saiman.instrument.acquisition.adq.ProductFamily;
import uk.co.saiman.instrument.acquisition.adq.TriggerMode;
import uk.co.saiman.instrument.acquisition.adq.impl.AdqDeviceManager.AdqLib;
import uk.co.saiman.log.Log;

@SuppressWarnings("unused")
public class WaveformAveragingStreamingAcquisition implements AdqAcquisition {
  private static final int FLAG_COMPENSATE_EXT_TRIG = 0x0001;
  private static final int FLAG_COMPENSATE_LEVEL_TRIG = 0x0002;

  private static final int FLAG_READOUT_FAST = 0x0004;
  private static final int FLAG_READOUT_MEDIUM = 0x0008;
  private static final int FLAG_READOUT_SLOW = 0x0010;

  private static final int FLAG_ENABLE_LEVEL_TRIGGER = 0x0020;

  private static final int FLAG_ENABLE_WAVEFORM_GET = 0x0040;

  private static final int FLAG_ENABLE_AUTOARMNREAD = 0x0080;

  private static final int FLAG_READOUT_A_ONLY = 0x0100;
  private static final int FLAG_READOUT_B_ONLY = 0x0200;
  private static final int FLAG_IMMEDIATE_READOUT = 0x0400;

  private static final int NUMBER_OF_CHANNELS = 1;
  private static final int NUMBER_OF_BUFFERS = 32;

  private final AdqLib lib;
  private final Pointer controlUnit;
  private final int deviceNumber;

  private final Unit<Dimensionless> sampleIntensityUnit;
  private final Unit<Time> sampleTimeUnit;

  private final int sampleDepth;
  private final int samplesPerRecord;
  private final int recordsPerAverage;
  private final int numberOfAverages;
  private final int numberOfChannels;
  private final int numberOfBuffers;

  private final TriggerMode triggerMode;
  private final ProductFamily productFamily;

  private final Pointer dataStream;
  private final Pointer[] dataTarget;

  private final double[] spectrumData;
  private final Consumer<SampledContinuousFunction<Time, Dimensionless>> dataObservable;

  private final Log log;

  public WaveformAveragingStreamingAcquisition(
      Adq114DeviceImpl device,
      AdqLib lib,
      Pointer controlUnit,
      int deviceNumber,
      Log log) {
    this.lib = lib;
    this.controlUnit = controlUnit;
    this.deviceNumber = deviceNumber;
    this.log = log;

    this.sampleIntensityUnit = device.getSampleIntensityUnit();
    this.sampleTimeUnit = device.getSampleTimeUnit();

    this.sampleDepth = device.getSampleDepth();
    this.samplesPerRecord = ((int) Math.ceil(sampleDepth / 128.0)) * 128;
    this.recordsPerAverage = device.getAccumulationsPerAcquisition();
    this.numberOfAverages = device.getAcquisitionCount();
    this.numberOfChannels = NUMBER_OF_CHANNELS;
    this.numberOfBuffers = NUMBER_OF_BUFFERS;

    this.triggerMode = device.getTriggerMode();
    this.productFamily = device.getProductFamily();

    lib.ADQ_DisarmTrigger(controlUnit, deviceNumber);

    lib.ADQ_SetTriggerMode(controlUnit, deviceNumber, device.getTriggerMode().toInt());
    check(
        lib.ADQ_SetTestPatternMode(controlUnit, deviceNumber, device.getTestPatternMode().toInt()));
    check(
        lib.ADQ_SetTestPatternConstant(controlUnit, deviceNumber, device.getTestPatternConstant()));

    int clockSource = 0;
    check(lib.ADQ_SetClockSource(controlUnit, deviceNumber, clockSource));
    check(lib.ADQ_SetPllFreqDivider(controlUnit, deviceNumber, device.getPllDivider()));

    lib.ADQ_SetTransferTimeout(controlUnit, deviceNumber, 5000);

    int bufferSize = numberOfChannels * samplesPerRecord * Integer.BYTES;
    check(lib.ADQ_SetTransferBuffers(controlUnit, deviceNumber, numberOfBuffers, bufferSize));

    int flags = getWfaFlags();
    check(
        lib
            .ADQ_WaveformAveragingSetup(
                controlUnit,
                deviceNumber,
                recordsPerAverage,
                samplesPerRecord,
                0,
                0,
                flags));

    check(
        lib.ADQ_SetDataFormat(controlUnit, deviceNumber, Adq114DataFormat.UNPACKED_32BIT.toInt()));

    try {
      Thread.sleep(20);
    } catch (InterruptedException e) {}

    check(lib.ADQ_SetStreamStatus(controlUnit, deviceNumber, 0x7));
    check(lib.ADQ_StartStreaming(controlUnit, deviceNumber));

    check(lib.ADQ_WaveformAveragingArm(controlUnit, deviceNumber));

    lib.ADQ_WaveformAveragingStartReadout(controlUnit, deviceNumber);

    this.dataStream = new Memory(bufferSize);
    this.dataTarget = new Pointer[AdqDeviceImpl.MAX_CHANNEL_COUNT];
    this.spectrumData = new double[sampleDepth];
    this.dataObservable = device::nextData;
  }

  protected int getWfaFlags() {
    int flags = 0;
    if (productFamily == V5) {
      flags |= FLAG_READOUT_SLOW;
    }

    switch (triggerMode) {
    case EXTERNAL_1:
      flags |= FLAG_COMPENSATE_EXT_TRIG;
      break;
    case LEVEL:
      flags |= FLAG_COMPENSATE_LEVEL_TRIG;
      break;
    default:
    }

    flags |= FLAG_ENABLE_AUTOARMNREAD;

    // flags |= FLAG_IMMEDIATE_READOUT;

    return flags;
  }

  @Override
  public void close() {
    lib.ADQ_WaveformAveragingShutdown(controlUnit, deviceNumber);

    try {
      Pointer inIdle = new Memory(Byte.BYTES);
      int counter = 2000;
      do {
        log.log(INFO, "purging buffers");

        if (triggerMode == SOFTWARE) {
          for (int t = 0; t < recordsPerAverage; t++) {
            check(lib.ADQ_SWTrig(controlUnit, deviceNumber));
          }
        }

        while (nextData()) {}

        lib.ADQ_WaveformAveragingGetStatus(controlUnit, deviceNumber, null, null, inIdle);
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {}
        if (counter-- < 0) {
          throw new AcquisitionException("Failed to shutdown WFA");
        }
      } while (inIdle.getByte(0) == 0);
    } finally {
      lib.ADQ_SetStreamStatus(controlUnit, deviceNumber, 0);
      lib.ADQ_DisarmTrigger(controlUnit, deviceNumber);
      lib.ADQ_WaveformAveragingDisarm(controlUnit, deviceNumber);
      lib.ADQ_SetDataFormat(controlUnit, deviceNumber, 0);
    }
  }

  @Override
  public void acquire() {
    int failureCounter = 0;
    int recordCounter = 0;

    for (int i = 0; i < numberOfChannels; i++) {
      dataTarget[i] = new Memory(samplesPerRecord * Integer.BYTES);
    }

    while (recordCounter < numberOfAverages) {
      log.log(INFO, "WFA Record " + recordCounter);

      if (nextData()) {
        recordCounter++;

      } else {
        // No buffers filled yet, retry
        if (++failureCounter > 32) {
          throw new AcquisitionException("No data received");
        }
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {}
      }
    }
  }

  private boolean nextData() {
    trigger();

    boolean bufferReady = isReadyTransferStatus(lib, controlUnit, deviceNumber);

    int lastError = lib.ADQ_GetLastError(controlUnit, deviceNumber);
    if (lastError != 0) {
      throw new AcquisitionException("Failed to perform transfer: " + lastError);
    }

    int streamingOverflow = lib.ADQ_GetStreamOverflow(controlUnit, deviceNumber);
    if (streamingOverflow != 0) {
      throw new AcquisitionException("Streaming overflow");
    }

    if (bufferReady) {
      collectData();
    }

    return bufferReady;
  }

  private void trigger() {
    // If trigger mode is software trigger ....
    // ...issue enough software triggers to
    // produce a single averaged record
    if (triggerMode == SOFTWARE) {
      for (int t = 0; t < recordsPerAverage; t++) {
        check(lib.ADQ_SWTrig(controlUnit, deviceNumber));
      }
    }
  }

  private void collectData() {
    int collectResult = lib.ADQ_CollectDataNextPage(controlUnit, deviceNumber);
    if (collectResult == 0) {
      throw new AcquisitionException("Failed to collect data from buffer");
    }

    int resultError = lib.ADQ_GetLastError(controlUnit, deviceNumber);
    if (resultError != 0) {
      throw new AcquisitionException("Failed to collect data from buffer: " + resultError);
    }

    int bufferSize = samplesPerRecord * Integer.BYTES * numberOfChannels;
    var sourceBuffer = lib.ADQ_GetPtrStream(controlUnit, deviceNumber).getByteBuffer(0, bufferSize);
    var sinkBuffer = dataStream.getByteBuffer(0, bufferSize);
    sinkBuffer.put(sourceBuffer);

    // Separate the raw streaming data of data_stream into the respective
    // channels
    // in data_target
    lib
        .ADQ_WaveformAveragingParseDataStream(
            controlUnit,
            deviceNumber,
            samplesPerRecord,
            dataStream,
            dataTarget);

    var data = dataStream;
    // var data = dataTarget[0];

    int byteOffset = 0;
    for (int i = 0; i < sampleDepth; i++) {
      int value = data.getInt(byteOffset);
      spectrumData[i] = value;
      // System.out.println(Integer.toBinaryString(value));
      byteOffset += Integer.BYTES;
    }

    dataObservable
        .accept(
            new ArraySampledContinuousFunction<>(
                new RegularSampledDomain<>(second().micro().getUnit(), sampleDepth, 1, 0),
                count().getUnit(),
                spectrumData));
  }

  private boolean isReadyTransferStatus(AdqLib lib, Pointer controlUnit, int deviceNumber) {
    var buffersFilledPointer = new Memory(Integer.BYTES);
    check(lib.ADQ_GetTransferBufferStatus(controlUnit, deviceNumber, buffersFilledPointer));
    final int buffersFilled = buffersFilledPointer.getInt(0);

    log.log(INFO, String.format("    Buffers currently filled: %3d", buffersFilled));

    return buffersFilled > 0;
  }

  protected void check(int result) {
    if (result == 0) {
      int lastError = lib.ADQ_GetLastError(controlUnit, deviceNumber);
      throw new AcquisitionException("Bad result: " + lastError);
    }
  }
}
