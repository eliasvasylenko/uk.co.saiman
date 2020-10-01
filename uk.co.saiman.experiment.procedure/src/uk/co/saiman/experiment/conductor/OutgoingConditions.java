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
 * This file is part of uk.co.saiman.experiment.conductor.
 *
 * uk.co.saiman.experiment.conductor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.conductor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.conductor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.executor.Evaluation;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;

class OutgoingConditions {
  private final Lock lock;
  private final WorkspaceExperimentPath path;
  private final Map<Class<?>, OutgoingCondition<?>> conditionPreparations;

  public OutgoingConditions(Lock lock, WorkspaceExperimentPath path) {
    this.lock = lock;
    this.path = path;
    this.conditionPreparations = new HashMap<>();
  }

  public void addOutgoingCondition(Class<?> type, Evaluation evaluation) {}

  public <T> Optional<OutgoingCondition<T>> getOutgoingCondition(Class<T> type) {
    @SuppressWarnings("unchecked")
    var preparation = (OutgoingCondition<T>) conditionPreparations.get(type);
    return Optional.ofNullable(preparation);
  }

  Lock lock() {
    return lock;
  }

  WorkspaceExperimentPath path() {
    return path;
  }

  public void update(Instruction instruction, Environment environment) {
    instruction.conditionPreparations().forEach(type -> {
      conditionPreparations
          .put(
              type,
              new OutgoingCondition<>(
                  OutgoingConditions.this,
                  type,
                  instruction.conditionPreparationEvaluation(type)));
    });

  }

  public void invalidate() {
    conditionPreparations.values().forEach(OutgoingCondition::invalidate);
  }

  public void terminate() {
    conditionPreparations.values().forEach(OutgoingCondition::terminate);
  }
}
