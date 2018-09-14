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
package uk.co.saiman.experiment;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import uk.co.saiman.data.resource.Location;

/**
 * A result store defines a strategy for locating, reading, and writing
 * experiment result data, typically for persistent storage.
 * 
 * @author Elias N Vasylenko
 */
public interface ResultStore {
  interface Storage {
    void dispose() throws IOException;

    Location location();
  }

  /**
   * Given a node whose {@link #locateStorage(ExperimentNode) arranged storage
   * location} may have changed, move stored result data from the previous
   * location to the arranged one.
   * <p>
   * The previous location have been given by a different {@link ResultStore
   * store} if a node was reparented, which could require expensive copying of
   * data. In other cases if the underlying storage mechanisms are compatible it
   * may be possible to determine that a faster strategy is available, e.g. a file
   * system move.
   * <p>
   * If the relocation fails, implementors should make a best-effort to leave the
   * data at the previous location intact. If the relocation succeeds, the data at
   * the previous location may be destroyed.
   * 
   * @param node
   * @param previousLocation
   * @throws IOException
   *           If the data could not be moved. Wherever possible exceptional
   *           termination should leave the result data intact at the original
   *           location.
   */
  default Storage relocateStorage(ExperimentNode<?, ?> node, Storage previousLocation)
      throws IOException {
    final int BUFFER_SIZE = 2048;

    Storage location = locateStorage(node);

    try {
      previousLocation.location().getResources().forEach(resource -> {
        try (ReadableByteChannel from = resource.read();
            WritableByteChannel to = location.location().getResource(resource.getName()).write()) {

          final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

          while (from.read(buffer) != -1) {
            buffer.flip();
            to.write(buffer);
            buffer.compact();
          }
          buffer.flip();
          while (buffer.hasRemaining()) {
            to.write(buffer);
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    } catch (RuntimeException e) {
      throw (IOException) e.getCause();
    }

    previousLocation.dispose();
    return location;
  }

  /**
   * Get the arranged storage location for a node. Subsequent invocations may
   * return a different location after certain {@link WorkspaceEvent workspace
   * events}, in which case an invocation of
   * {@link #relocateStorage(ExperimentNode, Storage)} is required in order to
   * move the data to the appropriate location.
   * 
   * @param node
   *          the node whose data storage we wish to locate
   * @return the location at which to store experiment results
   * @throws IOException
   *           if the storage is not accessible
   */
  Storage locateStorage(ExperimentNode<?, ?> node) throws IOException;
}
