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
import static uk.co.saiman.state.Accessor.stringAccessor;
import static uk.co.saiman.state.StateMap.empty;

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
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.storage.Storage;
import uk.co.saiman.experiment.storage.Store;
import uk.co.saiman.experiment.storage.filesystem.FileSystemStore.FileSystemStoreConfiguration;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;

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

  private static final MapIndex<Path> PATH = new MapIndex<>(
      "path",
      stringAccessor().map(p -> Paths.get(p), p -> p.toString()));

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
  public Storage allocateStorage(Path experimentRoot, ExperimentPath<Absolute> path) {
    return new PathStorage(getPath(experimentRoot, path));
  }

  @Override
  public Storage relocateStorage(
      Path experimentRoot,
      ExperimentPath<Absolute> path,
      Storage previousStorage)
      throws IOException {
    if (previousStorage.location() instanceof PathLocation) {
      Path previousPath = ((PathLocation) previousStorage.location()).getPath();
      Path newPath = getPath(experimentRoot, path);

      if (Files.exists(previousPath)) {
        Files.move(previousPath, newPath);
      }

      return new PathStorage(newPath);
    }
    return Store.super.relocateStorage(experimentRoot, path, previousStorage);
  }

  private Path getPath(Path experimentRoot, ExperimentPath<Absolute> path) {
    return path.ids().reduce(rootPath.resolve(experimentRoot), (p, i) -> p.resolve(i), null);
  }
}
