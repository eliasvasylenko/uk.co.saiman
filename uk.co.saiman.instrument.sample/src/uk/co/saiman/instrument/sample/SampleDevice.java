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

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.saiman.instrument.Device;
import uk.co.saiman.observable.ObservableValue;

/**
 * A hardware devices which manages sample exchange and analysis.
 * 
 * This abstraction should be general enough to be applicable to any sample
 * source with some sort of sample selection/navigation mechanism, e.g. a system
 * of inlet valves or a motor stage.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          The analysis location space.
 */
public interface SampleDevice<T> extends Device {
  @Override
  SampleController<T> acquireControl(long timeout, TimeUnit unit)
      throws TimeoutException, InterruptedException;

  /**
   * In general no guarantee is made that a request will be fulfilled (or will
   * fail) in a timely manner. Implementors may choose, however, to make such
   * promises and to enter the {@link Failed failed} state if they are unable to
   * fulfil them within some timeout period.
   * 
   * @return the currently requested sample state
   */
  ObservableValue<Optional<RequestedSampleState<T>>> requestedSampleState();

  ObservableValue<SampleState<T>> sampleState();

  /**
   * Generally for a positional stage this should be a straightforward check that
   * the given position is within the bounds of the stage, or is otherwise a valid
   * sample location. Implementations of this method should be idempotent and free
   * of side-effects.
   * 
   * @param location
   * @return true if the given sample location is valid for analysis and can be
   *         reached, false otherwise
   */
  boolean isPositionReachable(T location);

  /**
   * The actual measured sample location.
   * <p>
   * For different configurations of hardware the sample position may be defined
   * with slightly different behavior.
   * <p>
   * For instance, if the sample position may be selected over a continuous domain
   * then the implementation may report changes in sample position reflecting the
   * "motion" of the hardware as it approaches requested position. It may also
   * define an error tolerance for the measured location compared to the requested
   * location. As a result, when a {@link #requestedSampleState() requested}
   * analysis position is {@link #sampleState() reached}, its
   * {@link Analysis#position() position} may not be exactly
   * {@link Object#equals(Object) equal} to the {@link #samplePosition() measured
   * position}.
   * <p>
   * Alternatively, if the sample position may be selected over a discrete domain
   * then the implementation may not report any sample position at all until the
   * requested position has been reached.
   * 
   * @return an observable over the actual sample location
   */
  ObservableValue<Optional<T>> samplePosition();
}
