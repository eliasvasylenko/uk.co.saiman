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
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
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
import uk.co.saiman.experiment.ExperimentRoot;
import uk.co.saiman.experiment.impl.ExperimentLocationManager;
import uk.co.saiman.experiment.impl.ExperimentNodeImpl;

public class FileSystemLocationManager implements ExperimentLocationManager {
  private final Path rootPath;

  public FileSystemLocationManager(Path rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public void removeLocation(ExperimentNodeImpl<?, ?> node) throws IOException {
    Files.delete(getPath(node));
  }

  @Override
  public void updateLocation(ExperimentNodeImpl<?, ?> node, String id) throws IOException {
    Path newLocation = resolvePath(node, id);

    if (node.getId() != null) {
      Path oldLocation = getPath(node);

      if (Files.exists(oldLocation)) {
        Files.move(oldLocation, newLocation);
      }
    }
  }

  @Override
  public Location getLocation(ExperimentNodeImpl<?, ?> node) {
    return new PathLocation(getPath(node));
  }

  private Path getPath(ExperimentNode<?, ?> node) {
    return resolvePath(node, node.getId());
  }

  private Path getParentPath(ExperimentNode<?, ?> node) {
    return node.getParent().map(this::getPath).orElse(rootPath);
  }

  private Path resolvePath(ExperimentNode<?, ?> node, String id) {
    Path parentPath = getParentPath(node);

    if (!(node.getType() instanceof ExperimentRoot))
      parentPath = parentPath.resolve(node.getTypeId());
    return parentPath.resolve(id);
  }

  public Path getRootPath() {
    return rootPath;
  }
}
