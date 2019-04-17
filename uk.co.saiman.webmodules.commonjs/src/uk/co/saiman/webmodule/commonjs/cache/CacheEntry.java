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
 * This file is part of uk.co.saiman.webmodules.commonjs.
 *
 * uk.co.saiman.webmodules.commonjs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.commonjs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule.commonjs.cache;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newOutputStream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import uk.co.saiman.webmodule.commonjs.RegistryResolutionException;

public class CacheEntry {
  private final Path location;

  public CacheEntry(Path location) {
    this.location = location;
  }

  public Path getLocation() {
    return location;
  }

  public Path writeBytes(byte[] bytes) {
    return writeBytesImpl(getLocation(), bytes);
  }

  public Path writeBytes(String resourceName, byte[] bytes) {
    return writeBytesImpl(getLocation().resolve(resourceName), bytes);
  }

  public Path writeBytes(Path destination, byte[] bytes) {
    return writeBytesImpl(getLocation().resolve(destination), bytes);
  }

  private Path writeBytesImpl(Path destination, byte[] bytes) {
    try {
      createDirectories(destination.getParent());

      try (BufferedOutputStream buffered = new BufferedOutputStream(newOutputStream(destination))) {
        buffered.write(bytes);
        buffered.flush();
      }

      return destination;
    } catch (IOException e) {
      e.printStackTrace();
      throw new RegistryResolutionException(
          "Failed to write bytes to cache directory " + destination,
          e);
    }
  }

  /**
   * In case two instances are trying to write to the same location, i.e. one or
   * more registry instances handling multiple resource with the same sha1, we
   * must guard by writing to a unique, safe location then moving to the cache
   * location when done. If we find something is there by the time we're finished
   * we can just discard our work and use the existing entry.
   */
  void complete() {
    // TODO
  }
}
