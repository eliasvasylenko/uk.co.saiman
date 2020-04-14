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

import java.util.function.Function;

/**
 * An enumeration of the possible states for a {@link SampleDevice sample
 * device}.
 * 
 * @author Elias N Vasylenko
 */
public abstract class SampleState<T> {
  private static final Failed<?> FAILED = new Failed<>();
  private static final Transition<?> TRANSITION = new Transition<>();
  private static final Ready<?> READY = new Ready<>();
  private static final Exchange<?> EXCHANGE = new Exchange<>();

  SampleState() {}

  @SuppressWarnings("unchecked")
  public static <T> Failed<T> failed() {
    return (Failed<T>) FAILED;
  }

  @SuppressWarnings("unchecked")
  public static <T> Transition<T> transition() {
    return (Transition<T>) TRANSITION;
  }

  @SuppressWarnings("unchecked")
  public static <T> Ready<T> ready() {
    return (Ready<T>) READY;
  }

  @SuppressWarnings("unchecked")
  public static <T> Exchange<T> exchange() {
    return (Exchange<T>) EXCHANGE;
  }

  public static <T> Analysis<T> analysis(T position) {
    return new Analysis<>(position);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  @SuppressWarnings("unchecked")
  public static <T, U> SampleState<U> map(
      SampleState<T> state,
      Function<? super T, ? extends U> mapping) {
    if (state instanceof Analysis<?>) {
      var analysis = (Analysis<T>) state;
      var position = mapping.apply(analysis.position());

      if (position == null) {
        return failed();

      } else {
        return new Analysis<U>(position);
      }

    } else {
      return (SampleState<U>) state;
    }
  }
}
