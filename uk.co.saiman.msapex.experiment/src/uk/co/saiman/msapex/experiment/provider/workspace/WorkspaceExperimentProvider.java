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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.provider.workspace;

import java.nio.file.Path;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;

import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.definition.ExperimentDefinition;
import uk.co.saiman.experiment.graph.ExperimentId;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.storage.filesystem.FileSystemStore;
import uk.co.saiman.msapex.experiment.NewFileSystemExperimentDialog;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;
import uk.co.saiman.msapex.experiment.provider.ExperimentProvider;
import uk.co.saiman.msapex.experiment.workspace.Workspace;

public class WorkspaceExperimentProvider implements ExperimentProvider {
  @Inject
  @Localize
  ExperimentProperties text;

  @Inject
  @Optional
  Workspace workspace;
  @Inject
  @Optional
  FileSystemStore store;

  @Override
  public Stream<Experiment> createExperiments() {
    return new NewFileSystemExperimentDialog(workspace, text)
        .showAndWait()
        .map(ExperimentId::fromName)
        .map(
            id -> new Experiment(
                ExperimentDefinition.define(id),
                new StorageConfiguration<>(store, Path.of("."))))
        .stream();
  }
}
