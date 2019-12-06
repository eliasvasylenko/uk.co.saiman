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

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;

import com.sun.jna.Pointer;

import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.instrument.acquisition.adq.Adq114Control;
import uk.co.saiman.instrument.acquisition.adq.Adq114DataFormat;
import uk.co.saiman.instrument.acquisition.adq.Adq114Device;
import uk.co.saiman.instrument.acquisition.adq.impl.AdqDeviceManager.AdqLib;
import uk.co.saiman.observable.Observable;

public class Adq114DeviceImpl extends AdqDeviceImpl<Adq114Control> implements Adq114Device {
  private Adq114DataFormat dataFormat = Adq114DataFormat.PACKED_14BIT;

  public Adq114DeviceImpl(AdqDeviceManager manager) {
    super(manager);
  }

  public Adq114DeviceImpl(AdqDeviceManager manager, String serialNumber) {
    super(manager, serialNumber);
  }

  @Override
  public Adq114DataFormat getDataFormat() {
    return dataFormat;
  }

  @Override
  public void stopAcquisition() {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isAcquiring() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Unit<Dimensionless> getSampleIntensityUnit() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Unit<Time> getSampleTimeUnit() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SampledContinuousFunction<Time, Dimensionless> getLastAcquisitionData() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Observable<SampledContinuousFunction<Time, Dimensionless>> dataEvents() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getAcquisitionCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Quantity<Time> getSampleResolution() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Quantity<Frequency> getSampleFrequency() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Quantity<Time> getAcquisitionTime() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getSampleDepth() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  protected Adq114Control createController(ControlContext context) {
    return new Adq114ControlImpl(context);
  }

  public class Adq114ControlImpl extends AdqControlImpl implements Adq114Control {
    public Adq114ControlImpl(ControlContext context) {
      super(context);
    }

    @Override
    public void setDataFormat(Adq114DataFormat dataFormat) {
      Adq114DeviceImpl.this.dataFormat = dataFormat;
    }

    @Override
    public void setAcquisitionCount(int count) {
      // TODO Auto-generated method stub

    }

    @Override
    public void setAcquisitionTime(Quantity<Time> time) {
      // TODO Auto-generated method stub

    }

    @Override
    public void setSampleDepth(int depth) {
      // TODO Auto-generated method stub

    }

    @Override
    protected void startAcquisition(AdqLib lib, Pointer controlUnit, int deviceNumber) {
      try (var acquisition = new WaveformAveragingStreamingAcquisition(
          Adq114DeviceImpl.this,
          lib,
          controlUnit,
          deviceNumber)) {
        acquisition.acquire();
      }
    }
  }
}
