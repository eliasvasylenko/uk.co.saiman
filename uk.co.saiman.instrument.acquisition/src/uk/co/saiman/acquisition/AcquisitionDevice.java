/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.acquisition;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;

import uk.co.saiman.data.RegularSampledDomain;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.instrument.HardwareDevice;
import uk.co.strangeskies.observable.Observable;

/**
 * Software module for acquisition of {@link SampledContinuousFunction} data
 * through some mechanism. Typically continuous function data may correspond to
 * a mass spectrum.
 * 
 * @author Elias N Vasylenko
 */
public interface AcquisitionDevice extends HardwareDevice {
  /**
   * Begin an acquisition experiment with the current configuration.
   * 
   * @throws IllegalStateException
   *           if acquisition is already in progress
   */
  void startAcquisition();

  /**
   * Stop any acquisition experiment that may be in progress.
   */
  void stopAcquisition();

  /**
   * @return true if the module is currently in acquisition, false otherwise
   */
  boolean isAcquiring();

  /**
   * @return the units of measurement of sample intensities
   */
  Unit<Dimensionless> getSampleIntensityUnits();

  /**
   * @return the units of measurement of sample times
   */
  Unit<Time> getSampleTimeUnits();

  /**
   * @return The last acquired acquisition data. This leaves the format of the
   *         acquired data to the discretion of the implementing hardware
   *         module.
   */
  SampledContinuousFunction<Time, Dimensionless> getLastAcquisitionData();

  /**
   * Add or remove data event observers.
   * <p>
   * The observers may be triggered with data events that happen outside the
   * scope of an actual acquisition experiment, in the case of an "always on"
   * instrument setup. In this case, the {@link #isAcquiring()} method will
   * indicate whether the event is related to an experiment if invoked by a
   * listener to a data event.
   * 
   * @return an observable interface for registering data event listeners
   */
  Observable<SampledContinuousFunction<Time, Dimensionless>> dataEvents();

  /**
   * Add or remove acquisition data event observers.
   * <p>
   * This method operates as {@link #dataEvents()} except that the observation
   * does not begin until the next acquisition is in progress, and does not end
   * until that acquisition is complete.
   * 
   * @return an observable interface for registering data event listeners
   */
  default Observable<SampledContinuousFunction<Time, Dimensionless>> acquisitionDataEvents() {
    return dataEvents().dropWhile(m -> !isAcquiring()).takeWhile(m -> isAcquiring());
  }

  /**
   * Set the total acquisition count for a single experiment.
   * 
   * @param count
   *          the number of continua to acquire for a single experiment
   */
  void setAcquisitionCount(int count);

  /**
   * Get the total acquisition count for a single experiment.
   * 
   * @return the number of continua to acquire for a single experiment
   */
  int getAcquisitionCount();

  /**
   * Get the time resolution between each sample in the acquired sampled
   * continuous function. Unless otherwise specified by a subclass this may be
   * considered to be a constant.
   * 
   * @return the sample resolution in milliseconds
   */
  Quantity<Time> getSampleResolution();

  /**
   * Get the sample frequency in the acquired sampled continuous function.
   * Unless otherwise specified by a subclass this may be considered to be a
   * constant.
   * 
   * @return the sample frequency
   */
  Quantity<Frequency> getSampleFrequency();

  /**
   * Set the active sampling duration for a single data acquisition event. This
   * may adjust the acquisition depth to fit according to the current
   * acquisition resolution.
   * 
   * @param time
   *          the time an acquisition will last in milliseconds
   */
  void setAcquisitionTime(Quantity<Time> time);

  /**
   * Get the active sampling duration for a single data acquisition event.
   * 
   * @return the time an acquisition will last in milliseconds
   */
  Quantity<Time> getAcquisitionTime();

  /**
   * Set the number of samples in an acquired sampled continuous function. This
   * may adjust the acquisition time to fit according to the current acquisition
   * resolution.
   * 
   * @param depth
   *          the sample depth for an acquired data array
   */
  void setSampleDepth(int depth);

  /**
   * Get the number of samples in an acquired sampled continuous function.
   * 
   * @return the sample depth for an acquired data array
   */
  int getSampleDepth();

  /**
   * Get the sample domain of an acquired spectrum.
   * 
   * @return a {@link RegularSampledDomain} object which will be consistent with
   *         any acquired spectra
   */
  default RegularSampledDomain<Time> getSampleDomain() {
    return new RegularSampledDomain<>(
        getSampleTimeUnits(),
        getSampleDepth(),
        getSampleFrequency()
            .to(getSampleTimeUnits().inverse().asType(Frequency.class))
            .getValue()
            .doubleValue(),
        0);
  }
}
