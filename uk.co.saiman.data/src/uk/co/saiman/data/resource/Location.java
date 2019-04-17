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
import java.util.stream.Stream;

public interface Location {
  /**
   * Get all resources at the location.
   * 
   * @return a stream of resources which {@link Resource#exists() exist} at the
   *         location
   * @throws IOException
   */
  Stream<Resource> resources() throws IOException;

  Resource getResource(String name) throws IOException;

  /**
   * This is only a convenience method to concatenate the name and extension
   * around a dot. We cannot reliably extract an extension from a filename, as
   * sometimes the logical extension itself contains a dot, and sometimes so does
   * the name, so we cannot determine where to split the string. Because of this
   * the representation of a resource name is flattened to a single string.
   * 
   * @param name      the name of the file
   * @param extension the extension of the file
   * @return a resource with the derived name [name].[extension]
   * @throws IOException
   */
  default Resource getResource(String name, String extension) throws IOException {
    return getResource(name + "." + extension);
  }

  /**
   * @return a string indicating the type of location and specifying it if
   *         possible
   */
  @Override
  String toString();
}
