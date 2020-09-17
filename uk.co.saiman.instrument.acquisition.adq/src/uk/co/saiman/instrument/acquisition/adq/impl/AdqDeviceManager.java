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

import static uk.co.saiman.log.Log.Level.INFO;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import uk.co.saiman.instrument.acquisition.adq.AdqException;
import uk.co.saiman.instrument.acquisition.adq.AdqProductId;
import uk.co.saiman.instrument.acquisition.adq.TraceLevel;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

@Component(service = AdqDeviceManager.class, immediate = true)
public class AdqDeviceManager implements AutoCloseable {
  public interface AdqLib extends Library {
    /*
     * Control Unit API
     */

    Pointer CreateADQControlUnit();

    void DeleteADQControlUnit(Pointer controlUnit);

    int ADQAPI_GetRevision();

    int ADQControlUnit_FindDevices(Pointer controlUnit);

    String ADQ_GetBoardSerialNumber(Pointer controlUnit, int adqNumber);

    String ADQ_GetCardOption(Pointer controlUnit, int adqNumber);

    int ADQControlUnit_GetFailedDeviceCount(Pointer controlUnit);

    int ADQControlUnit_GetLastFailedDeviceError(Pointer controlUnit);

    int ADQControlUnit_EnableErrorTrace(
        Pointer controlUnit,
        int traceLevel,
        String traceFileDirectory);

    int ADQControlUnit_EnableErrorTraceAppend(
        Pointer controlUnit,
        int traceLevel,
        String traceFileDirectory);

    /*
     * ADQ_API
     */

    int ADQ_GetProductID(Pointer controlUnit, int adqNumber);

    int ADQ_GetProductFamily(Pointer controlUnit, int deviceNumber, Pointer familyInt);

    Pointer ADQ_GetRevision(Pointer controlUnit, int deviceNumber);

    int ADQ_Blink(Pointer controlUnit, int deviceNumber);

    int ADQ_GetErrorVector(Pointer controlUnit, int deviceNumber);

    int ADQ_GetLastError(Pointer controlUnit, int deviceNumber);

    int ADQ_IsAlive(Pointer controlUnit, int deviceNumber);

    int ADQ_ReBootADQFromFlash(Pointer controlUnit, int deviceNumber);

    int ADQ_RunRecorderSelfTest(Pointer controlUnit, int deviceNumber, Pointer inoutVector);

    int ADQ_IsUSBDevice(Pointer controlUnit, int deviceNumber);

    int ADQ_IsUSB3Device(Pointer controlUnit, int deviceNumber);

    int ADQ_IsPCIeDevice(Pointer controlUnit, int deviceNumber);

    int ADQ_SetTriggerMode(Pointer controlUnit, int deviceNumber, int triggerMode);

    int ADQ_GetTriggerMode(Pointer controlUnit, int deviceNumber);

    int ADQ_DisarmTrigger(Pointer controlUnit, int deviceNumber);

    int ADQ_ArmTrigger(Pointer controlUnit, int deviceNumber);

    int ADQ_SWTrig(Pointer controlUnit, int deviceNumber);

    int ADQ_GetAcquiredAll(Pointer controlUnit, int deviceNumber);

    int ADQ_SetTestPatternMode(Pointer controlUnit, int deviceNumber, int testPatternMode);

    int ADQ_SetTestPatternConstant(Pointer controlUnit, int deviceNumber, int testPatternConstant);

    int ADQ_MultiRecordSetup(
        Pointer controlUnit,
        int deviceNumber,
        int recordCount,
        int samplesPerRecord);

    int ADQ_MultiRecordClose(Pointer controlUnit, int deviceNumber);

    int ADQ_GetStreamStatus(Pointer controlUnit, int deviceNumber);

    int ADQ_SetStreamStatus(Pointer controlUnit, int deviceNumber, int status);

    int ADQ_StartStreaming(Pointer controlUnit, int deviceNumber);

    int ADQ_SetClockSource(Pointer controlUnit, int deviceNumber, int clockSource);

    int ADQ_SetPllFreqDivider(Pointer controlUnit, int deviceNumber, int pllDivider);

    int ADQ_GetData(
        Pointer controlUnit,
        int deviceNumber,
        Pointer[] buffers,
        int bufferSize,
        int bytesPerSample,
        int startRecord,
        int recordCount,
        int channelsMask,
        int startSample,
        int samplesCount,
        int transferMode);

    int ADQ_SetDataFormat(Pointer controlUnit, int deviceNumber, int dataFormat);

    int ADQ_CollectDataNextPage(Pointer controlUnit, int deviceNumber);

    int ADQ_GetSamplesPerPage(Pointer controlUnit, int deviceNumber);

    int ADQ_GetStreamOverflow(Pointer controlUnit, int deviceNumber);

    Pointer ADQ_GetPtrStream(Pointer controlUnit, int deviceNumber);

    int ADQ_WaveformAveragingArm(Pointer controlUnit, int deviceNumber);

    int ADQ_WaveformAveragingDisarm(Pointer controlUnit, int deviceNumber);

    int ADQ_WaveformAveragingStartReadout(Pointer controlUnit, int deviceNumber);

    int ADQ_WaveformAveragingShutdown(Pointer controlUnit, int deviceNumber);

    int ADQ_GetTransferBufferStatus(Pointer controlUnit, int deviceNumber, Memory buffersFilled);

    int ADQ_FlushDMA(Pointer controlUnit, int deviceNumber);

    int ADQ_WaveformAveragingGetStatus(
        Pointer controlUnit,
        int deviceNumber,
        Pointer /* char */ ready,
        Pointer /* int */ recordsCompleted,
        Pointer /* char */ inIdle);

    int ADQ_SetTransferTimeout(Pointer controlUnit, int deviceNumber, int milliseconds);

    int ADQ_WaveformAveragingParseDataStream(
        Pointer controlUnit,
        int deviceNumber,
        int samplesPerRecord,
        Pointer dataStream,
        Pointer[] dataTarget);

    int ADQ_SetTransferBuffers(
        Pointer controlUnit,
        int deviceNumber,
        int numberOfBuffers,
        int bufferSize);

    int ADQ_WaveformAveragingSetup(
        Pointer controlUnit,
        int deviceNumber,
        int numberOfWaveforms,
        int samplesPerRecord,
        int preTrigSamples,
        int holdOffSamples,
        int flags);

    int ADQ_WaveformAveragingSoftwareTrigger(Pointer controlUnit, int deviceNumber);

    int ADQ_TriggeredStreamingSetupV5(
        Pointer controlUnit,
        int deviceNumber,
        int samplesPerRecord,
        int preTrigSamples,
        int holdOffSamples,
        int flags);/*-
                   int armMode,
                   int readOutSpeed,
                   int channel);*/

    int ADQ_TriggeredStreamingArmV5(Pointer controlUnit, int deviceNumber);

    int ADQ_TriggeredStreamingDisarmV5(Pointer controlUnit, int deviceNumber);

    int ADQ_SetTriggeredStreamingTotalNofRecords(
        Pointer controlUnit,
        int deviceNumber,
        int MaxNofRecordsTotal);

    int ADQ_GetTriggeredStreamingRecordSizeBytes(Pointer controlUnit, int deviceNumber);

    int ADQ_GetTriggeredStreamingHeaderSizeBytes(Pointer controlUnit, int deviceNumber);

    int ADQ_GetTriggeredStreamingRecords(
        Pointer controlUnit,
        int deviceNumber,
        int NofRecordsToRead,
        Pointer[] /* void **/ data_buf,
        Pointer /* void */ header_buf,
        Pointer /* int */ NofRecordsRead);

    int ADQ_TriggeredStreamingGetNofRecordsCompleted(
        Pointer controlUnit,
        int deviceNumber,
        int ChannelsMask,
        Pointer /* int */ NofRecordsCompleted);

    int ADQ_TriggeredStreamingGetStatusV5(
        Pointer controlUnit,
        int deviceNumber,
        Pointer /* char */ ready,
        Pointer /* int */ recordsCompleted,
        Pointer /* char */ inIdle);

    int ADQ_SetGainAndOffset(
        Pointer controlUnit,
        int deviceNumber,
        byte channel,
        int gain,
        int offset);

    int ADQ_GetGainAndOffset(
        Pointer controlUnit,
        int deviceNumber,
        byte channel,
        Pointer /* int */ gain,
        Pointer /* int */ offset);
  }

  private static final AdqLib LIB;

  static {
    LIB = Native.loadLibrary("adq", AdqLib.class);
  }

  private final Pointer controlUnit;

  private final Map<String, Integer> devices = new HashMap<>();

  private final Log log;

  public AdqDeviceManager(Log log) {
    this.log = log;

    controlUnit = getWithLib(AdqLib::CreateADQControlUnit);
    if (controlUnit == null) {
      throw new IllegalStateException("Failed to create control unit");
    } else {
      log.log(INFO, "API revision: " + AdqDeviceManager.getApiRevision());
    }
  }

  public void setLog(TraceLevel traceLevel, Path logPath) {
    if (!(Files.isDirectory(logPath) || Files.isRegularFile(logPath))) {
      throw new IllegalArgumentException("Invalid log path %s, must be file or directory");
    }
    Objects.requireNonNull(traceLevel);
    var errorTrace = getWithLib(
        lib -> lib
            .ADQControlUnit_EnableErrorTrace(
                controlUnit,
                TraceLevel.INFO.ordinal(),
                logPath.toAbsolutePath().toString()));
    if (errorTrace != 1) {
      var e = new AdqException("Failed to open " + traceLevel + " log at " + logPath);
      log.log(Level.ERROR, e);
      throw e;
    }
  }

  @Override
  @Deactivate
  public void close() {
    runWithLib(lib -> lib.DeleteADQControlUnit(controlUnit));
  }

  Pointer getControlUnit() {
    return controlUnit;
  }

  synchronized Stream<String> getDevices(AdqProductId productId) {
    refreshDevices();
    return devices
        .entrySet()
        .stream()
        .filter(e -> getProductId(e.getValue()) == productId)
        .map(Entry::getKey);
  }

  private synchronized void refreshDevices() {
    runWithLib(lib -> {
      int deviceCount = lib.ADQControlUnit_FindDevices(controlUnit);

      if (lib.ADQControlUnit_GetFailedDeviceCount(controlUnit) > 0) {
        var e = new AdqException(
            "Failed to get devices " + lib.ADQControlUnit_GetLastFailedDeviceError(controlUnit));
        log.log(Level.ERROR, e);
        throw e;
      }

      devices.clear();
      for (int i = 1; i <= deviceCount; i++) {
        var serialNumber = lib.ADQ_GetBoardSerialNumber(controlUnit, i);

        devices.put(serialNumber, i);
      }
    });
  }

  synchronized int getDeviceNumber(String serialNumber) {
    var deviceNumber = devices.get(serialNumber);
    if (deviceNumber == null) {
      refreshDevices();
      deviceNumber = devices.get(serialNumber);
    }
    return deviceNumber;
  }

  synchronized AdqProductId getProductId(int deviceNumber) {
    return AdqProductId.fromId(getWithLib(lib -> lib.ADQ_GetProductID(controlUnit, deviceNumber)));
  }

  public static int getApiRevision() {
    return getWithLib(AdqLib::ADQAPI_GetRevision);
  }

  static void runWithLib(Consumer<AdqLib> action) {
    getWithLib(lib -> {
      action.accept(lib);
      return null;
    });
  }

  static <T> T getWithLib(Function<AdqLib, T> action) {
    return action.apply(LIB);
  }
}
