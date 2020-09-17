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

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;
import uk.co.saiman.state.StateMap;

/**
 * A result store defines a strategy for locating, reading, and writing
 * experiment result data, typically for persistent storage.
 * 
 * @author Elias N Vasylenko
 */
public interface Store<T> {
  T configure(StateMap stateMap);

  StateMap deconfigure(T store);

  /**
   * Arrange a location to allocate storage for an experiment path.
   * 
   * @param node
   *          the node whose data storage we wish to locate
   * @return the location at which to store experiment results
   * @throws IOException
   *           if the storage is not accessible
   */
  Storage allocateStorage(T configuration, WorkspaceExperimentPath path) throws IOException;

  /**
   * Given a node whose {@link #allocateStorage(Object, ExperimentPath) arranged
   * store location} may have changed, move stored result data from the previous
   * location to the arranged one.
   * <p>
   * The previous location have been given by a different
   * {@link StorageConfiguration store} if a node was reparented, which could
   * require expensive copying of data. In other cases if the underlying storage
   * mechanisms are compatible it may be possible to determine that a faster
   * strategy is available, e.g. a file system move.
   * <p>
   * If the relocation fails, implementors should make a best-effort to leave the
   * data at the previous location intact. If the relocation succeeds, the data at
   * the previous location may be destroyed.
   * 
   * @param node
   * @param previousStorage
   * @throws IOException
   *           If the data could not be moved. Wherever possible exceptional
   *           termination should leave the result data intact at the original
   *           location.
   */
  default Storage relocateStorage(
      T configuration,
      WorkspaceExperimentPath path,
      Storage previousStorage) throws IOException {
    Storage storage = allocateStorage(configuration, path);

    try {
      previousStorage.location().resources().forEach(resource -> {
        try {
          resource.transfer(storage.location());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    } catch (RuntimeException e) {
      throw (IOException) e.getCause();
    }

    previousStorage.deallocate();
    return storage;
  }
}
