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
package uk.co.saiman.experiment.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import uk.co.saiman.data.resource.Location;
import uk.co.saiman.data.resource.PathLocation;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProcedure;
import uk.co.saiman.experiment.ResultStore;

public class FileSystemResultStore implements ResultStore {
  public class PathStorage implements Storage {
    private final PathLocation location;

    public PathStorage(Path path) {
      this.location = new PathLocation(path);
    }

    @Override
    public void dispose() throws IOException {
      Files.deleteIfExists(location.getPath());
    }

    @Override
    public Location location() {
      return location;
    }
  }

  private final Path rootPath;

  public FileSystemResultStore(Path rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public Storage locateStorage(ExperimentNode<?, ?> node) {
    return new PathStorage(getPath(node));
  }

  @Override
  public Storage relocateStorage(ExperimentNode<?, ?> node, Storage previousLocation)
      throws IOException {
    if (previousLocation.location() instanceof PathLocation) {
      Path previousPath = ((PathLocation) previousLocation.location()).getPath();
      Path newPath = getPath(node);

      if (Files.exists(previousPath)) {
        Files.move(previousPath, newPath);
      }

      return new PathStorage(newPath);
    }
    return ResultStore.super.relocateStorage(node, previousLocation);
  }

  private Path getPath(ExperimentNode<?, ?> node) {
    return resolvePath(node, node.getId());
  }

  private Path getParentPath(ExperimentNode<?, ?> node) {
    return node.getParent().map(this::getPath).orElseGet(this::getRootPath);
  }

  private Path resolvePath(ExperimentNode<?, ?> node, String id) {
    Path parentPath = getParentPath(node);

    if (!(node.getProcedure() instanceof ExperimentProcedure))
      parentPath = parentPath.resolve(node.getProcedure().getId());
    return parentPath.resolve(id);
  }

  public Path getRootPath() {
    return rootPath;
  }
}
