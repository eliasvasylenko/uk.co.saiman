/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.util.Objects;

/**
 * An analysis location was requested, and was reached. The
 * {@link SampleDevice#samplePosition() location} of the device should be valid.
 * <p>
 * Depending on the type of hardware this may only indicate that the analysis
 * location is reached within a certain tolerance. Therefore this state does not
 * necessarily indicate that the {@link SampleDevice#samplePosition() actual}
 * and {@link SampleDevice#requestedSampleState() requested} locations are
 * {@link #equals(Object) exactly equal}.
 */
public class Analysis<T> extends RequestedSampleState<T> {
  private final T position;

  Analysis(T position) {
    this.position = position;
  }

  public T position() {
    return position;
  }

  @Override
  public String toString() {
    return super.toString() + "(" + position + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || (obj.getClass() != Analysis.class)) {
      return false;
    }
    Analysis<?> that = (Analysis<?>) obj;
    return Objects.equals(this.position, that.position);
  }

  @Override
  public int hashCode() {
    return Objects.hash(position);
  }
}
