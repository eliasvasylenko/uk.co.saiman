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
package uk.co.saiman.instrument.acquisition;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

public interface AcquisitionControl extends AutoCloseable {
  /**
   * Begin an acquisition experiment with the current configuration.
   * 
   * @throws IllegalStateException if acquisition is already in progress
   */
  void startAcquisition();

  /**
   * Set the total acquisition count for a single experiment.
   * 
   * @param count the number of continua to acquire for a single experiment
   */
  void setAcquisitionCount(int count);

  /**
   * Set the active sampling duration for a single data acquisition event. This
   * may adjust the acquisition depth to fit according to the current acquisition
   * resolution.
   * 
   * @param time the time an acquisition will last in milliseconds
   */
  void setAcquisitionTime(Quantity<Time> time);

  /**
   * Set the number of samples in an acquired sampled continuous function. This
   * may adjust the acquisition time to fit according to the current acquisition
   * resolution.
   * 
   * @param depth the sample depth for an acquired data array
   */
  void setSampleDepth(int depth);

  @Override
  void close();
}
