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
 * This file is part of uk.co.saiman.data.
 *
 * uk.co.saiman.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data.resource;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * A resource should typically represent a single logical resource location,
 * such as a file path or a URL, which should be fully described by the
 * resources {@link #getName() name} and {@link #getLocation() location}. It
 * should be immutable in this representation.
 * 
 * @author Elias N Vasylenko
 *
 */
public interface Resource {
  Location getLocation();

  String getName();

  default boolean hasExtension(String extension) {
    return getName().endsWith("." + extension);
  }

  Resource transfer(Resource destination) throws IOException;

  default Resource transfer(Location destination) throws IOException {
    return transfer(destination.getResource(getName()));
  }

  ReadableByteChannel read() throws IOException;

  WritableByteChannel write() throws IOException;

  ByteChannel open() throws IOException;

  public boolean exists();

  /**
   * Create the resource if it does not exist.
   * 
   * @throws IOException
   */
  void create() throws IOException;

  /**
   * Delete the resource if exists.
   * 
   * @throws IOException
   */
  void delete() throws IOException;
}
