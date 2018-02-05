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
 * This file is part of uk.co.saiman.comms.copley.
 *
 * uk.co.saiman.comms.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.simulation;

import static java.lang.Double.compare;
import static java.lang.Math.abs;
import static uk.co.saiman.comms.copley.VariableBank.ACTIVE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class InterpolatedVariable<T> extends ComputedVariable<T> {
  private final ReferenceVariable<T> target;
  private final double speed;

  private final Function<? super T, ? extends Double> toDouble;
  private final Function<? super Double, ? extends T> fromDouble;

  private final List<Long> lastTime;
  private final List<Double> lastPosition;

  public InterpolatedVariable(
      int axes,
      ReferenceVariable<T> target,
      double speed,
      Function<? super T, ? extends Double> toDouble,
      Function<? super Double, ? extends T> fromDouble) {
    super(target.getConverter());

    this.target = target;
    this.speed = speed;

    this.fromDouble = fromDouble;
    this.toDouble = toDouble;

    lastTime = new ArrayList<>(axes);
    lastPosition = new ArrayList<>(axes);

    for (int i = 0; i < axes; i++) {
      lastTime.add(0l);
      lastPosition.add(0d);
    }
  }

  @Override
  public T compute(int axis) {
    try {
      long lastTime = this.lastTime.get(axis);
      double lastPosition = this.lastPosition.get(axis);

      long currentTime = System.currentTimeMillis();
      double targetPosition = toDouble.apply(target.getReference(axis, ACTIVE));
      double deltaPosition = (int) ((currentTime - lastTime) * speed);

      double currentPosition;
      if (deltaPosition > abs(lastPosition - targetPosition)) {
        currentPosition = targetPosition;
      } else {
        currentPosition = lastPosition - compare(lastPosition, targetPosition) * deltaPosition;
      }

      this.lastTime.set(axis, currentTime);
      this.lastPosition.set(axis, currentPosition);

      return fromDouble.apply(currentPosition);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
