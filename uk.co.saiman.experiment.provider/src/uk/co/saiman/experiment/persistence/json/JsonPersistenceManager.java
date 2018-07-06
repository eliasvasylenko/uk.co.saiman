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
package uk.co.saiman.experiment.persistence.json;

import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newDirectoryStream;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import uk.co.saiman.data.resource.PathResource;
import uk.co.saiman.data.resource.Resource;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.impl.ExperimentPersistenceManager;
import uk.co.saiman.experiment.impl.PersistedExperiment;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.persistence.impl.PersistedExperimentImpl;

public class JsonPersistenceManager implements ExperimentPersistenceManager {
  private static final String FILE_EXTENSION = ".ejsn";

  private final Path rootPath;
  private final PathMatcher fileMatcher;

  private final Map<PersistedExperiment, JsonPersistedStateDocument> rootDocuments;
  private final Map<String, PersistedExperiment> rootStates;

  private final List<ExperimentType<?, ?>> experimentTypes;

  private boolean loaded;

  public JsonPersistenceManager(
      Path rootPath,
      Collection<? extends ExperimentType<?, ?>> experimentTypes) {
    this.rootPath = rootPath;
    this.fileMatcher = rootPath.getFileSystem().getPathMatcher("glob:**/*" + FILE_EXTENSION);

    this.rootDocuments = new HashMap<>();
    this.rootStates = new HashMap<>();

    this.experimentTypes = new ArrayList<>(experimentTypes);
  }

  protected void loadPersistedExperimentStates() throws IOException {
    rootStates.clear();
    loaded = true;

    try (DirectoryStream<Path> stream = newDirectoryStream(
        rootPath,
        file -> isRegularFile(file) && fileMatcher.matches(file))) {

      for (Path path : stream) {
        JsonPersistedStateDocument document = new JsonPersistedStateDocument(
            new PathResource(path));
        document.load();

        PersistedExperiment experiment = new PersistedExperimentImpl(document.getPersistedState()) {
          @Override
          public void setId(String id) throws IOException {
            moveExperiment(this, id);
          }
        };

        rootStates.put(experiment.getId(), experiment);
        rootDocuments.put(experiment, document);
      }
    }
  }

  @Override
  public Stream<ExperimentType<?, ?>> getExperimentTypes() {
    return experimentTypes.stream();
  }

  @Override
  public Stream<PersistedExperiment> getExperiments() throws IOException {
    if (!loaded)
      loadPersistedExperimentStates();
    return rootStates.values().stream();
  }

  @Override
  public PersistedExperiment addExperiment(String id, String typeId, PersistedState configuration)
      throws IOException {
    if (configuration == null)
      configuration = new PersistedState();

    JsonPersistedStateDocument document = new JsonPersistedStateDocument(
        getResource(id),
        configuration);

    PersistedExperiment experiment = new PersistedExperimentImpl(
        document.getPersistedState(),
        id,
        typeId) {
      @Override
      public void setId(String id) throws IOException {
        super.setId(id);
        moveExperiment(this, id);
      }
    };

    rootStates.put(id, experiment);
    rootDocuments.put(experiment, document);

    return experiment;
  }

  protected void moveExperiment(PersistedExperiment experiment, String id) throws IOException {
    rootDocuments.get(experiment).getResource().delete();
    rootDocuments
        .get(experiment)
        .setResource(new PathResource(rootPath.resolve(id + FILE_EXTENSION)));
  }

  @Override
  public void removeExperiment(PersistedExperiment experiment) throws IOException {
    rootDocuments.get(experiment).getResource().delete();
  }

  protected Resource getResource(String id) {
    return new PathResource(rootPath.resolve(id + FILE_EXTENSION));
  }
}
