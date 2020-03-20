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
 * This file is part of uk.co.saiman.experiment.storage.
 *
 * uk.co.saiman.experiment.storage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.storage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.storage;

import java.io.IOException;

import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;

/**
 * A result store defines a strategy for locating, reading, and writing
 * experiment result data, typically for persistent storage.
 * 
 * @author Elias N Vasylenko
 */
public abstract class DelegatingStore<T, U> implements Store<T> {
  private final Store<U> delegate;

  public DelegatingStore(Store<U> delegate) {
    this.delegate = delegate;
  }

  protected abstract U configureDelegate(T configuration, WorkspaceExperimentPath path);

  public Storage allocateStorage(T configuration, WorkspaceExperimentPath path) throws IOException {
    return delegate.allocateStorage(configureDelegate(configuration, path), path);
  }

  public Storage relocateStorage(
      T configuration,
      WorkspaceExperimentPath path,
      Storage previousStorage) throws IOException {
    return delegate.relocateStorage(configureDelegate(configuration, path), path, previousStorage);
  }
}
