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
package uk.co.saiman.acquisition;

import java.util.Set;

import uk.co.saiman.mathematics.Interval;

/**
 * A configuration interface over a set of time ranges describing pass and fail
 * gates for acquisition.
 * <p>
 * Acquisition events, or periods of acquisition data, will be accepted or
 * rejected based on their containment within these ranges.
 * 
 * @author Elias N Vasylenko
 */
public interface AcquisitionGates {
  /**
   * The acquisition module this gate configuration interface is associated with.
   * 
   * @return An {@link AcquisitionDevice} instance which can be configured via
   *         this {@link AcquisitionGates} instance.
   */
  AcquisitionDevice<?> acquisitionModule();

  /**
   * Get the time before which acquisition data should be ignored. This is
   * absolute regardless of any gates which overlap with this period.
   * 
   * @return The start time of useful acquisition data in milliseconds
   */
  double getStartTime();

  /**
   * Set the time before which acquisition data should be ignored. This is
   * absolute regardless of any gates which overlap with this period.
   * 
   * @param newStartTime The start time of useful acquisition data in milliseconds
   */
  void setStartTime(double newStartTime);

  /**
   * @return The set of pass gates participating in this acquisition gate set.
   */
  Set<Interval<Double>> getPassGates();

  /**
   * @param gates The set of pass gates to participate in this acquisition gate
   *              set.
   */
  void setPassGates(Set<Interval<Double>> gates);

  /**
   * @return The set of fail gates participating in this acquisition gate set.
   */
  Set<Interval<Double>> getFailGates();

  /**
   * @param gates The set of fail gates to participate in this acquisition gate
   *              set.
   */
  void setFailGates(Set<Interval<Double>> gates);

  /**
   * Determine whether the given time passes the set of gate filters.
   * <p>
   * Membership is true of a time when the number of pass gates containing that
   * time, subtracting the number of fail gates containing that time, is greater
   * or equal to 0.
   * 
   * @param time The time we wish to check
   * @return True if the given time passes the gate filter, false otherwise
   */
  default boolean passes(double time) {
    return (time > getStartTime())
        && getPassGates().stream().filter(r -> r.contains(time)).count()
            - getFailGates().stream().filter(r -> r.contains(time)).count() >= 0;
  }
}
