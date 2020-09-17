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
import uk.co.saiman.instrument.acquisition.adq.ProductFamily;
import uk.co.saiman.instrument.acquisition.adq.TransferMode;
import uk.co.saiman.instrument.acquisition.adq.TriggerMode;
import uk.co.saiman.instrument.acquisition.adq.impl.AdqDeviceManager.AdqLib;
import uk.co.saiman.log.Log;

@SuppressWarnings("unused")
public class MultiRecordAcquisition implements AdqAcquisition {
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

  private final Pointer[] dataTarget;

  private final double[] spectrumData;
  private final Consumer<SampledContinuousFunction<Time, Dimensionless>> dataObservable;

  private final Log log;

  public MultiRecordAcquisition(
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

    lib.ADQ_SetTriggerMode(controlUnit, deviceNumber, device.getTriggerMode().toInt());
    check(
        lib.ADQ_SetTestPatternConstant(controlUnit, deviceNumber, device.getTestPatternConstant()));
    check(
        lib.ADQ_SetTestPatternMode(controlUnit, deviceNumber, device.getTestPatternMode().toInt()));

    int clockSource = 0;
    check(lib.ADQ_SetClockSource(controlUnit, deviceNumber, clockSource));
    check(lib.ADQ_SetPllFreqDivider(controlUnit, deviceNumber, device.getPllDivider()));

    // check(
    // lib.ADQ_SetDataFormat(controlUnit, deviceNumber,
    // Adq114DataFormat.UNPACKED_14BIT.toInt()));

    try {
      Thread.sleep(20);
    } catch (InterruptedException e) {}

    this.dataTarget = new Pointer[8];
    this.spectrumData = new double[sampleDepth];
    this.dataObservable = device::nextData;

    check(lib.ADQ_MultiRecordSetup(controlUnit, deviceNumber, recordsPerAverage, samplesPerRecord));
  }

  @Override
  public void close() {
    lib.ADQ_DisarmTrigger(controlUnit, deviceNumber);
    lib.ADQ_MultiRecordClose(controlUnit, deviceNumber);
    lib.ADQ_SetDataFormat(controlUnit, deviceNumber, 0);
  }

  @Override
  public void acquire() {
    int failureCounter = 0;
    int recordCounter = 0;

    for (int i = 0; i < numberOfChannels; i++) {
      dataTarget[i] = new Memory(recordsPerAverage * samplesPerRecord * Short.BYTES);
    }

    while (recordCounter < numberOfAverages) {
      log.log(INFO, "Record " + recordCounter);

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
    check(lib.ADQ_DisarmTrigger(controlUnit, deviceNumber));
    check(lib.ADQ_ArmTrigger(controlUnit, deviceNumber));

    boolean bufferReady;
    do {
      if (triggerMode == SOFTWARE) {
        check(lib.ADQ_SWTrig(controlUnit, deviceNumber));
      }
      bufferReady = lib.ADQ_GetAcquiredAll(controlUnit, deviceNumber) != 0;
    } while (!bufferReady);

    int lastError = lib.ADQ_GetLastError(controlUnit, deviceNumber);
    if (lastError != 0) {
      throw new AcquisitionException("Failed to perform transfer: " + lastError);
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
  }

  private void collectData() {
    check(
        lib
            .ADQ_GetData(
                controlUnit,
                deviceNumber,
                dataTarget,
                recordsPerAverage * samplesPerRecord,
                Short.BYTES,
                0,
                recordsPerAverage,
                1,
                0,
                samplesPerRecord,
                TransferMode.NORMAL.toInt()));

    var data = dataTarget[0];

    for (int j = 0; j < sampleDepth; j++) {
      spectrumData[j] = 0;
    }
    for (int i = 0; i < recordsPerAverage; i++) {
      int byteOffset = samplesPerRecord * i * Short.BYTES;

      for (int j = 0; j < sampleDepth; j++) {
        int value = 0 | data.getShort(byteOffset);

        spectrumData[j] += value;
        // System.out.println(Integer.toBinaryString(value));
        byteOffset += Short.BYTES;
      }
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
