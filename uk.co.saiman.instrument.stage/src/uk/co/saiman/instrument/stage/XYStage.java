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
 * This file is part of uk.co.saiman.instrument.stage.
 *
 * uk.co.saiman.instrument.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.measure.quantity.Length;

import uk.co.saiman.measurement.coordinate.XYCoordinate;

public interface XYStage extends Stage<XYCoordinate<Length>> {
  @Override
  XYStageController acquireControl(long timeout, TimeUnit unit)
      throws TimeoutException, InterruptedException;

  /**
   * 
   * @return The minimally containing lower bound of all
   *         {@link #isPositionReachable(Object) reachable} positions of the
   *         stage. The location of the bound itself may not be reachable, for
   *         example if the reachable area is circular. Implementations of this
   *         method should be idempotent and free of side-effects.
   */
  XYCoordinate<Length> getLowerBound();

  /**
   * @return The minimally containing upper bound of all
   *         {@link #isPositionReachable(Object) reachable} positions of the
   *         stage. The location of the bound itself may not be reachable, for
   *         example if the reachable area is circular. Implementations of this
   *         method should be idempotent and free of side-effects.
   */
  XYCoordinate<Length> getUpperBound();
}
