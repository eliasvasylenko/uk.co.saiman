/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.instrument.acquisition.
 *
 * uk.co.saiman.instrument.acquisition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.acquisition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.vacuum;

import java.util.Optional;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Time;

import uk.co.saiman.instrument.Device;
import uk.co.saiman.observable.Observable;

/**
 * Software module for control and monitoring of vacuum.
 * 
 * @author Elias N Vasylenko
 */
public interface VacuumDevice<T extends VacuumControl> extends Device<T> {

  /**
   * @return the units of measurement of sample intensities
   */
  Unit<Pressure> getPressureMeasurementUnit();

  /**
   * @return the units of measurement of sample times
   */
  Unit<Time> getSampleTimeUnit();

  /**
   * @return The last acquired acquisition data. This leaves the format of the
   *         acquired data to the discretion of the implementing hardware module.
   */
  Optional<VacuumSample> getLastSample();

  /**
   * Add or remove sample event observers.
   * 
   * @return an observable interface for registering data event listeners
   */
  Observable<VacuumSample> sampleEvents();

  /**
   * Get the time resolution between each vacuum sample.
   * 
   * @return the sample resolution
   */
  Quantity<Time> getSampleResolution();

  /**
   * Get the frequency of vacuum samples
   * 
   * @return the sample frequency
   */
  Quantity<Frequency> getSampleFrequency();
}
