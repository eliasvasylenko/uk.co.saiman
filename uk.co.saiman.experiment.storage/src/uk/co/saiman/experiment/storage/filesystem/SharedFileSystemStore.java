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
package uk.co.saiman.experiment.storage.filesystem;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.state.StateMap.empty;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.storage.DelegatingStore;
import uk.co.saiman.experiment.storage.Store;
import uk.co.saiman.experiment.storage.filesystem.SharedFileSystemStore.FileSystemStoreConfiguration;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;
import uk.co.saiman.state.StateMap;

@Designate(ocd = FileSystemStoreConfiguration.class, factory = true)
@Component(
    name = SharedFileSystemStore.SHARED_FILE_SYSTEM_STORE_ID,
    configurationPolicy = REQUIRE,
    service = { Store.class, SharedFileSystemStore.class })
public class SharedFileSystemStore extends DelegatingStore<Void, Path> {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Shared File System Store Configuration",
      description = "The configuration for a shared file-system-based experiment result store")
  public @interface FileSystemStoreConfiguration {
    @AttributeDefinition(name = "Store Root", description = "The root path for the store")
    String rootPath();
  }

  public static final String SHARED_FILE_SYSTEM_STORE_ID = "uk.co.saiman.experiment.store.filesystem.shared";

  private final Path rootPath;

  @Activate
  public SharedFileSystemStore(FileSystemStoreConfiguration configuration) {
    this(Paths.get(configuration.rootPath()));
  }

  public SharedFileSystemStore(Path rootPath) {
    super(new FileSystemStore());
    this.rootPath = rootPath;
  }

  public Path getRootPath() {
    return rootPath;
  }

  @Override
  public Void configure(StateMap stateMap) {
    return null;
  }

  @Override
  public StateMap deconfigure(Void configuration) {
    return empty();
  }

  @Override
  protected Path configureDelegate(Void configuration, WorkspaceExperimentPath path) {
    return rootPath.resolve(path.getExperimentId().toString());
  }
}
