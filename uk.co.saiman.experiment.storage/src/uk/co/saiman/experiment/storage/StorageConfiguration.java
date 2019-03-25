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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.storage;

import java.io.IOException;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.state.StateMap;

/**
 * A store defines a strategy for arranging locations to read, and write
 * experiment result data, typically for persistent storage.
 * 
 * @author Elias N Vasylenko
 */
public class StorageConfiguration<T> {
  private final Store<T> store;
  private final T configuration;

  public Store<T> store() {
    return store;
  }

  public T configuration() {
    return configuration;
  }

  public StorageConfiguration(Store<T> store, StateMap persistedState) {
    this.store = store;
    this.configuration = store.configure(persistedState);
  }

  public StorageConfiguration(Store<T> storage, T configuration) {
    this.store = storage;
    this.configuration = configuration;
  }

  public Storage relocateStorage(ExperimentPath<Absolute> path, Storage previousStore)
      throws IOException {
    return store.relocateStorage(configuration, path, previousStore);
  }

  public Storage locateStorage(ExperimentPath<Absolute> path) throws IOException {
    return store.allocateStorage(configuration, path);
  }

  public StateMap deconfigure() {
    return store.deconfigure(configuration);
  }
}
