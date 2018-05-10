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
 * This file is part of uk.co.saiman.instrument.sample.
 *
 * uk.co.saiman.instrument.sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.sample;

/**
 * An enumeration of the possible states for a {@link SampleDevice sample
 * device}.
 * 
 * The {@link #EXCHANGE_REQUESTED} state may only be succeeded by
 * {@link #EXCHANGE} or {@link #EXCHANGE_FAILED}, while the
 * {@link #ANALYSIS_LOCATION_REQUESTED} state may only be succeeded by
 * {@link #ANALYSIS} or {@link #ANALYSIS_LOCATION_FAILED}.
 * 
 * The {@link #EXCHANGE_REQUESTED} and {@link #ANALYSIS_LOCATION_REQUESTED}
 * states may only be preceded by a state in which the hardware is stopped.
 * 
 * Conversely, a state in which the hardware is stopped should typically be
 * stable, i.e. should only be succeeded by {@link #EXCHANGE_REQUESTED} or
 * {@link #ANALYSIS_LOCATION_REQUESTED}. However in the event of hardware
 * failure or physical interference it is possible for the {@link #EXCHANGE} or
 * {@link #ANALYSIS} states to transition directly into {@link #EXCHANGE_FAILED}
 * or {@link #ANALYSIS_LOCATION_FAILED} respectively.
 * 
 * @author Elias N Vasylenko
 */
public enum SampleState {
  /**
   * Moving into exchange configuration. Hardware may be operational.
   */
  EXCHANGE_REQUESTED,

  /**
   * Exchange configuration not reached. Hardware stopped.
   */
  EXCHANGE_FAILED,

  /**
   * Exchange configuration reached. Hardware stopped.
   */
  EXCHANGE,

  /**
   * Moving into analysis configuration. Hardware may be operational.
   */
  ANALYSIS_LOCATION_REQUESTED,

  /**
   * Analysis configuration not reached. Hardware stopped.
   */
  ANALYSIS_LOCATION_FAILED,

  /**
   * Analysis configuration reached. Hardware stopped.
   * <p>
   * Depending on the type of hardware this may only indicate that the analysis
   * location is reached within a certain tolerance. Therefore this state does not
   * necessarily indicate that the {@link SampleDevice#actualLocation() requested}
   * and {@link SampleDevice#requestedLocation() actual} locations are
   * {@link #equals(Object) exactly equal}.
   */
  ANALYSIS
}
