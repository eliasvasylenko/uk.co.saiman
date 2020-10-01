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

public class OutgoingResults {
  private final Lock lock;
  private final WorkspaceExperimentPath path;
  private final Map<Class<?>, OutgoingResult<?>> resultPreparations;

  public OutgoingResults(Lock lock, WorkspaceExperimentPath path) {
    this.lock = lock;
    this.path = path;
    this.resultPreparations = new HashMap<>();
  }

  public void addOutgoingResult(Class<?> type, Evaluation evaluation) {}

  public <T> Optional<OutgoingResult<T>> getOutgoingResult(Class<T> type) {
    @SuppressWarnings("unchecked")
    var preparation = (OutgoingResult<T>) resultPreparations.get(type);
    return Optional.ofNullable(preparation);
  }

  Lock lock() {
    return lock;
  }

  WorkspaceExperimentPath path() {
    return path;
  }

  public void update(Instruction instruction, Environment environment) {
    instruction.resultObservations().forEach(type -> {
      resultPreparations.put(type, new OutgoingResult<>(OutgoingResults.this, type));
    });
  }

  public void invalidate() {
    resultPreparations.values().forEach(OutgoingResult::invalidate);
  }

  public void terminate() {
    resultPreparations.values().forEach(OutgoingResult::terminate);
  }
}
