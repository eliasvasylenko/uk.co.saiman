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
 * This file is part of uk.co.saiman.experiment.declaration.
 *
 * uk.co.saiman.experiment.declaration is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.declaration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.dependency;

import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;

/**
 * A condition which may be produced by a preparation during the conducting of
 * an experiment procedure.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the data type of the condition
 */
public interface Condition<T> extends Product<T>, AutoCloseable {
  static enum ConditionStatus {
    /**
     * The condition is not yet prepared
     */
    READY,
    /**
     * The condition is being prepared and is available to be consumed
     */
    PREPARING,
    /**
     * The condition was prepared and is no longer available to be consumed
     */
    PREPARED,
    /**
     * The condition was not prepared, something went wrong
     */
    FAILED
  }

  ConditionStatus status();

  @Override
  ConditionPath<Absolute, T> path();

  T value();

  @Override
  void close();
}
