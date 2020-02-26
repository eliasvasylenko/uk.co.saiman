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

import static java.util.Objects.requireNonNull;
import static uk.co.saiman.instrument.acquisition.adq.AdqHardwareInterface.PCIE;
import static uk.co.saiman.instrument.acquisition.adq.AdqHardwareInterface.USB;
import static uk.co.saiman.instrument.acquisition.adq.AdqHardwareInterface.USB3;
import static uk.co.saiman.instrument.acquisition.adq.FpgaTarget.ALG_FPGA;
import static uk.co.saiman.instrument.acquisition.adq.FpgaTarget.COMM_FPGA;
import static uk.co.saiman.instrument.acquisition.adq.ProductFamily.K7;
import static uk.co.saiman.instrument.acquisition.adq.ProductFamily.V5;
import static uk.co.saiman.instrument.acquisition.adq.ProductFamily.V6;
import static uk.co.saiman.instrument.acquisition.adq.RevisionInformation.LOCAL_COPY;
import static uk.co.saiman.instrument.acquisition.adq.RevisionInformation.MIXED;
import static uk.co.saiman.instrument.acquisition.adq.RevisionInformation.SVN_MANAGED;
import static uk.co.saiman.instrument.acquisition.adq.RevisionInformation.SVN_UPDATED;
import static uk.co.saiman.log.Log.Level.INFO;
import static uk.co.saiman.measurement.Units.second;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.instrument.ControllerImpl;
import uk.co.saiman.instrument.DeviceImpl;
import uk.co.saiman.instrument.DeviceStatus;
import uk.co.saiman.instrument.acquisition.adq.AdqDevice;
import uk.co.saiman.instrument.acquisition.adq.AdqDevice.AdqControl;
import uk.co.saiman.instrument.acquisition.adq.AdqHardwareInterface;
import uk.co.saiman.instrument.acquisition.adq.FirmwareRevision;
import uk.co.saiman.instrument.acquisition.adq.FpgaTarget;
import uk.co.saiman.instrument.acquisition.adq.ProductFamily;
import uk.co.saiman.instrument.acquisition.adq.RevisionInformation;
import uk.co.saiman.instrument.acquisition.adq.TestPatternMode;
import uk.co.saiman.instrument.acquisition.adq.TriggerMode;
import uk.co.saiman.instrument.acquisition.adq.impl.AdqDeviceManager.AdqLib;
import uk.co.saiman.log.Log;
import uk.co.saiman.measurement.scalar.Scalar;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;

public abstract class AdqDeviceImpl<T extends AdqControl> extends DeviceImpl<T>
    implements AdqDevice<T> {
  protected static final int MAX_CHANNEL_COUNT = 8;

  protected interface AdqGetter<T> {
    public T perform(AdqLib lib, Pointer controlUnit, int deviceNumber);
  }

  protected interface AdqAction {
    public void perform(AdqLib lib, Pointer controlUnit, int deviceNumber);
  }

  private final ObservableProperty<DeviceStatus> status = new ObservablePropertyImpl<>();
  private final AdqDeviceManager manager;
  private String serialNumber;

  private int acquisitionCount = 1;
  private int sampleDepth = 1;
  private TriggerMode triggerMode = TriggerMode.SOFTWARE;
  private TestPatternMode testPatternMode = TestPatternMode.NORMAL_OPERATION;
  private int testPatternConstant = 0;

  private final HotObservable<SampledContinuousFunction<Time, Dimensionless>> dataObservable = new HotObservable<>();
  private final HotObservable<SampledContinuousFunction<Time, Dimensionless>> acquisitionDataObservable = new HotObservable<>();
  private boolean acquiring = false;

  private final Log log;

  protected AdqDeviceImpl(AdqDeviceManager manager, String serialNumber, Log log) {
    this.manager = requireNonNull(manager);
    this.serialNumber = serialNumber;
    this.log = requireNonNull(log);

    log.log(INFO, "Device ID: " + getProductId());
    log.log(INFO, "Serial No: " + getSerialNumber());
    log.log(INFO, "Product Family: " + getProductFamily());
    log.log(INFO, "Firmware ALG: " + getFirmwareRevisionFpga(ALG_FPGA));
    log.log(INFO, "Firmware COMM: " + getFirmwareRevisionFpga(COMM_FPGA));
    log.log(INFO, "Hardware Interface: " + getHardwareInterface());
  }

  @Override
  public String toString() {
    return getProductId().toString();
  }

  protected void setAcquisitionCount(int count) {
    this.acquisitionCount = count;
  }

  protected void setAcquisitionTime(Quantity<Time> time) {
    setSampleDepth(time.divide(getSampleResolution()).getValue().intValue());
  }

  protected void setSampleDepth(int depth) {
    this.sampleDepth = depth;
  }

  protected void setTriggerMode(TriggerMode triggerMode) {
    this.triggerMode = triggerMode;
  }

  protected void setTestPatternMode(TestPatternMode testPatternMode) {
    this.testPatternMode = testPatternMode;
  }

  protected void setTestPatternConstant(int testPatternConstant) {
    this.testPatternConstant = testPatternConstant;
  }

  @Override
  public double getGain(boolean relative) {
    return get((lib, controlUnit, deviceNumber) -> {
      var gain = new Memory(Integer.BYTES);
      var offset = new Memory(Integer.BYTES);
      int channel = 1;
      if (!relative) {
        channel = channel & (1 << 7);
      }
      lib.ADQ_GetGainAndOffset(controlUnit, deviceNumber, (byte) channel, gain, offset);
      return gain.getInt(0) / 1024d;
    });
  }

  @Override
  public int getOffset(boolean relative) {
    return get((lib, controlUnit, deviceNumber) -> {
      var gain = new Memory(Integer.BYTES);
      var offset = new Memory(Integer.BYTES);
      int channel = 1;
      if (!relative) {
        channel = channel & (1 << 7);
      }
      lib.ADQ_GetGainAndOffset(controlUnit, deviceNumber, (byte) channel, gain, offset);
      return offset.getInt(0);
    });
  }

  protected Log getLog() {
    return log;
  }

  protected void nextData(SampledContinuousFunction<Time, Dimensionless> data) {
    dataObservable.next(data);
    acquisitionDataObservable.next(data);
  }

  @Override
  public boolean isAcquiring() {
    return acquiring;
  }

  @Override
  public Observable<SampledContinuousFunction<Time, Dimensionless>> dataEvents() {
    return dataObservable;
  }

  @Override
  public Observable<SampledContinuousFunction<Time, Dimensionless>> acquisitionDataEvents() {
    return acquisitionDataObservable;
  }

  @Override
  public int getAcquisitionCount() {
    return acquisitionCount;
  }

  @Override
  public Quantity<Time> getSampleResolution() {
    return new Scalar<>(second().getUnit(), 1).divide(getSampleFrequency()).asType(Time.class);
  }

  @Override
  public Quantity<Time> getAcquisitionTime() {
    return getSampleResolution().multiply(sampleDepth);
  }

  @Override
  public int getSampleDepth() {
    return sampleDepth;
  }

  @Override
  public ObservableValue<DeviceStatus> status() {
    return status;
  }

  protected <U> U get(AdqGetter<U> getter) {
    return AdqDeviceManager.getWithLib(lib -> {
      initialize();
      var controlUnit = manager.getControlUnit();
      var deviceNumber = manager.getDeviceNumber(serialNumber);
      return getter.perform(lib, controlUnit, deviceNumber);
    });
  }

  protected void run(AdqAction action) {
    AdqDeviceManager.runWithLib(lib -> {
      initialize();
      var controlUnit = manager.getControlUnit();
      var deviceNumber = manager.getDeviceNumber(serialNumber);
      action.perform(lib, controlUnit, deviceNumber);
    });
  }

  public final void initialize() {
    if (serialNumber == null) {
      serialNumber = manager.getDevices(getProductId()).findFirst().get();
    }
  }

  @Override
  public FirmwareRevision getFirmwareRevisionFpga(FpgaTarget target) {
    return get((lib, controlUnit, deviceNumber) -> {
      ProductFamily productFamily = getProductFamily();

      int offset;
      if (target == FpgaTarget.ALG_FPGA) {
        if (productFamily == V6) {
          offset = 0;
        } else {
          offset = 3;
        }
      } else {
        if (productFamily == V6) {
          throw new IllegalArgumentException(
              "Product family " + V6 + " does not support FPGA target " + target);
        }
        offset = 0;
      }

      Pointer revision = lib.ADQ_GetRevision(controlUnit, deviceNumber);
      int revisionNumber = revision.getInt(0 + offset);
      int svnManaged = revision.getInt(1 + offset);
      int svnUpdated = revision.getInt(2 + offset);
      RevisionInformation revisionInformation = svnManaged == 0
          ? (svnUpdated == 0 ? SVN_UPDATED : SVN_MANAGED)
          : (svnUpdated == 0 ? LOCAL_COPY : MIXED);

      return new FirmwareRevision() {
        @Override
        public int revisionNumber() {
          return revisionNumber;
        }

        @Override
        public RevisionInformation information() {
          return revisionInformation;
        }

        @Override
        public String toString() {
          return revisionNumber() + ", " + information();
        }
      };
    });
  }

  @Override
  public String getSerialNumber() {
    initialize();
    return serialNumber;
  }

  @Override
  public TriggerMode getTriggerMode() {
    if (triggerMode == null) {
      triggerMode = TriggerMode.fromInt(get(AdqLib::ADQ_GetTriggerMode));
    }
    return triggerMode;
  }

  @Override
  public TestPatternMode getTestPatternMode() {
    return testPatternMode;
  }

  @Override
  public int getTestPatternConstant() {
    return testPatternConstant;
  }

  @Override
  public AdqHardwareInterface getHardwareInterface() {
    return get(
        (lib, controlUnit, deviceNumber) -> (lib.ADQ_IsPCIeDevice(controlUnit, deviceNumber) == 1)
            ? PCIE
            : (lib.ADQ_IsUSB3Device(controlUnit, deviceNumber) == 1) ? USB3 : USB);
  }

  @Override
  public ProductFamily getProductFamily() {
    return get((lib, controlUnit, deviceNumber) -> {
      Pointer family = new Memory(Integer.BYTES);

      lib.ADQ_GetProductFamily(controlUnit, deviceNumber, family);
      int familyId = family.getInt(0);
      switch (familyId) {
      case 5:
        return V5;
      case 6:
        return V6;
      case 7:
        return K7;
      default:
        throw new IllegalStateException("Unexpected product family id " + familyId);
      }
    });
  }

  public abstract class AdqControlImpl extends ControllerImpl implements AdqControl {
    public AdqControlImpl(ControlContext context) {
      super(context);
    }

    @Override
    public void setGainAndOffset(double gain, int offset, boolean relativeToDefault) {
      run((lib, controlUnit, deviceNumber) -> {
        int channel = 1;
        if (!relativeToDefault) {
          channel = channel & (1 << 7);
        }
        lib
            .ADQ_SetGainAndOffset(
                controlUnit,
                deviceNumber,
                (byte) channel,
                (int) (gain * 1024),
                offset);
      });
    }

    @Override
    public void setAcquisitionCount(int count) {
      AdqDeviceImpl.this.setAcquisitionCount(count);
    }

    @Override
    public void setAcquisitionTime(Quantity<Time> time) {
      AdqDeviceImpl.this.setAcquisitionTime(time);
    }

    @Override
    public void setSampleDepth(int depth) {
      AdqDeviceImpl.this.setSampleDepth(depth);
    }

    @Override
    public void setTriggerMode(TriggerMode triggerMode) {
      AdqDeviceImpl.this.setTriggerMode(triggerMode);
    }

    @Override
    public void setTestPatternMode(TestPatternMode testPatternMode) {
      AdqDeviceImpl.this.setTestPatternMode(testPatternMode);
    }

    @Override
    public void setTestPatternConstant(int testPatternConstant) {
      AdqDeviceImpl.this.setTestPatternConstant(testPatternConstant);
    }

    @Override
    public void softwareTrigger() {
      try (var lock = acquireLock()) {
        run(AdqLib::ADQ_SWTrig);
      }
    }

    @Override
    public void startAcquisition() {
      try (var lock = acquireLock()) {
        run((lib, controlUnit, deviceNumber) -> {
          try {
            acquiring = true;
            startAcquisition(lib, controlUnit, deviceNumber);
          } finally {
            acquisitionDataObservable.complete();
            acquiring = false;
            acquisitionDataObservable.start();
          }
        });
      }
    }

    protected abstract void startAcquisition(AdqLib lib, Pointer controlUnit, int deviceNumber);
  }
}
