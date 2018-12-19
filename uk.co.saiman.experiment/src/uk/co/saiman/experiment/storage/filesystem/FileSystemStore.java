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
package uk.co.saiman.experiment.storage.filesystem;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.experiment.state.Accessor.stringAccessor;
import static uk.co.saiman.experiment.state.StateMap.empty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.data.resource.Location;
import uk.co.saiman.data.resource.PathLocation;
import uk.co.saiman.experiment.ExperimentStep;
import uk.co.saiman.experiment.state.Accessor.PropertyAccessor;
import uk.co.saiman.experiment.storage.Storage;
import uk.co.saiman.experiment.storage.Store;
import uk.co.saiman.experiment.storage.filesystem.FileSystemStore.FileSystemStoreConfiguration;
import uk.co.saiman.experiment.state.StateMap;

@Designate(ocd = FileSystemStoreConfiguration.class, factory = true)
@Component(name = FileSystemStore.FILE_SYSTEM_STORE_ID, configurationPolicy = REQUIRE, service = {
    Store.class,
    FileSystemStore.class })
public class FileSystemStore implements Store<Path> {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "File System Store Configuration", description = "The configuration for a file-system-based experiment result store")
  public @interface FileSystemStoreConfiguration {
    @AttributeDefinition(name = "Store Root", description = "The default root path for the store, to resolve relative locations")
    String rootPath();
  }

  public static final String FILE_SYSTEM_STORE_ID = "uk.co.saiman.experiment.store.filesystem";

  public class PathStorage implements Storage {
    private final PathLocation location;

    public PathStorage(Path path) {
      this.location = new PathLocation(path);
    }

    @Override
    public void deallocate() throws IOException {
      Files.deleteIfExists(location.getPath());
    }

    @Override
    public Location location() {
      return location;
    }
  }

  private static final PropertyAccessor<Path> PATH = stringAccessor("path")
      .map(p -> Paths.get(p), p -> p.toString());

  private final Path rootPath;

  @Activate
  public FileSystemStore(FileSystemStoreConfiguration configuration) {
    this(Paths.get(configuration.rootPath()));
  }

  public FileSystemStore(Path rootPath) {
    this.rootPath = rootPath;
  }

  public Path getRootPath() {
    return rootPath;
  }

  @Override
  public Path configure(StateMap stateMap) {
    return stateMap.get(PATH);
  }

  @Override
  public StateMap deconfigure(Path configuration) {
    return empty().with(PATH, configuration);
  }

  @Override
  public Storage allocateStorage(Path configuration, ExperimentStep<?> node) {
    return new PathStorage(getPath(node, configuration));
  }

  @Override
  public Storage relocateStorage(
      Path configuration,
      ExperimentStep<?> node,
      Storage previousStorage)
      throws IOException {
    if (previousStorage.location() instanceof PathLocation) {
      Path previousPath = ((PathLocation) previousStorage.location()).getPath();
      Path newPath = getPath(node, configuration);

      if (Files.exists(previousPath)) {
        Files.move(previousPath, newPath);
      }

      return new PathStorage(newPath);
    }
    return Store.super.relocateStorage(configuration, node, previousStorage);
  }

  private Path getPath(ExperimentStep<?> node, Path path) {
    return resolvePath(node, path, node.getId());
  }

  private Path getParentPath(ExperimentStep<?> node, Path path) {
    return node.getParent().map(p -> getPath(p, path)).orElseGet(() -> rootPath.resolve(path));
  }

  private Path resolvePath(ExperimentStep<?> node, Path path, String id) {
    return getParentPath(node, path).resolve(id);
  }
}
