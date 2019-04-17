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

import java.util.concurrent.TimeUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;

import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.instrument.ConnectionState;
import uk.co.saiman.instrument.InstrumentRegistration;
import uk.co.saiman.instrument.acquisition.adq.Adq114Control;
import uk.co.saiman.instrument.acquisition.adq.Adq114Device;
import uk.co.saiman.instrument.acquisition.adq.AdqHardwareInterface;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.ObservableValue;

public class Adq114DeviceImpl extends AdqDeviceImpl<Adq114Control> implements Adq114Device {
  public Adq114DeviceImpl(AdqDeviceManager manager) {
    super(manager);
  }

  @Override
  public AdqHardwareInterface getHardwareInterface() {
    // TODO Auto-generated method stub
    return null;
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
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InstrumentRegistration getInstrumentRegistration() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ObservableValue<ConnectionState> connectionState() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Adq114Control acquireControl(long timeout, TimeUnit unit) {
    // TODO Auto-generated method stub
    return null;
  }
}
