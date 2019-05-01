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
 * This file is part of uk.co.saiman.experiment.procedure.
 *
 * uk.co.saiman.experiment.procedure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.procedure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.procedure;

import static java.lang.String.format;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.production.ProductPath;
import uk.co.saiman.experiment.production.Result;
import uk.co.saiman.experiment.production.Results;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.observable.Observable;

public class Conductor implements Results {
  private final StorageConfiguration<?> storageConfiguration;

  private final Map<ExperimentPath<?>, ExecutorProgress<?>> progress;
  private Procedure procedure;

  public Conductor(StorageConfiguration<?> storageConfiguration) {
    this.storageConfiguration = storageConfiguration;
    this.progress = new HashMap<>();
  }

  public StorageConfiguration<?> storageConfiguration() {
    return storageConfiguration;
  }

  public void conduct(Procedure procedure) {
    this.procedure = procedure;

    var progressIterator = progress.entrySet().iterator();
    while (progressIterator.hasNext()) {
      var progress = progressIterator.next();

      procedure
          .instruction(progress.getKey())
          .ifPresentOrElse(progress.getValue()::updateInstruction, () -> {
            progress.getValue().interrupt();
            progressIterator.remove();
          });
    }
    procedure
        .instructions()
        .filter(instruction -> progress.containsKey(instruction.path()))
        .forEach(
            instruction -> progress
                .put(instruction.path(), new ExecutorProgress<>(this, instruction)));
  }

  public Optional<Procedure> procedure() {
    return Optional.of(procedure);
  }

  public synchronized void interrupt() {
    // TODO cancel anything ongoing...
  }

  public synchronized void clear() {
    interrupt();

    try {
      storageConfiguration.locateStorage(ExperimentPath.defineAbsolute()).deallocate();
    } catch (IOException e) {
      throw new ConductorException(format("Unable to clear conducted procedure %s", procedure), e);
    }

    procedure = null;

  }

  @Override
  public Stream<Result<?>> results() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T extends Result<?>> T resolveResult(ProductPath<?, T> path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T extends Result<?>> Observable<T> results(ProductPath<?, T> path) {
    // TODO Auto-generated method stub
    return null;
  }
}
