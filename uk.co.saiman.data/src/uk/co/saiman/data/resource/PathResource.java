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

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static uk.co.saiman.bytes.Channels.pipe;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class PathResource implements Resource {
  private final Path path;

  public PathResource(Path path) {
    this.path = path;
  }

  public Path getPath() {
    return path;
  }

  @Override
  public String getName() {
    String name = path.getFileName().toString();
    int lastDot = name.lastIndexOf('.');
    return lastDot > 0 ? name.substring(0, lastDot - 1) : name;
  }

  @Override
  public boolean hasExtension(String extension) {
    return path.getFileName().toString().endsWith("." + extension);
  }

  @Override
  public Resource transfer(Resource destination) throws IOException {
    if (destination instanceof PathResource) {
      Path destinationPath = ((PathResource) destination).path;
      if (!path.equals(destinationPath)) {
        Files.move(path, destinationPath, ATOMIC_MOVE);
      }
    } else {
      destination.create();
      pipe(read(), destination.write());
      delete();
    }
    return destination;
  }

  @Override
  public ReadableByteChannel read() throws IOException {
    return Files.newByteChannel(path, READ);
  }

  @Override
  public WritableByteChannel write() throws IOException {
    Files.createDirectories(path.getParent());
    return Files.newByteChannel(path, WRITE, TRUNCATE_EXISTING);
  }

  @Override
  public ByteChannel open() throws IOException {
    return Files.newByteChannel(path, READ, WRITE);
  }

  @Override
  public Location getLocation() {
    return new PathLocation(path.getParent());
  }

  @Override
  public boolean exists() {
    return Files.exists(path);
  }

  @Override
  public void create() throws IOException {
    if (!Files.exists(path)) {
      Files.createDirectories(path.getParent());
      Files.createFile(path);
    }
  }

  @Override
  public void delete() throws IOException {
    Files.deleteIfExists(path);
  }
}
