/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex.i18n;

import java.nio.file.Path;

import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.msapex.workspace.Workspace;
import uk.co.saiman.experiment.msapex.workspace.WorkspaceExperiment;
import uk.co.saiman.properties.Localized;
import uk.co.saiman.properties.service.PropertiesService;

/**
 * Properties interface for texts relating to experiments.
 * 
 * @author Elias N Vasylenko
 */
@SuppressWarnings("javadoc")
@PropertiesService
public interface ExperimentProperties {
  Localized<String> newExperiment();

  Localized<String> newExperimentName();

  Localized<String> configuration();

  Localized<String> missingResult();

  Localized<String> missingExperimentType(String id);

  Localized<String> experimentRoot();

  Localized<String> overwriteData();

  Localized<String> overwriteDataConfirmation(Path newLocation);

  Localized<String> renameExperiment();

  Localized<String> renameExperimentName(ExperimentId name);

  Localized<String> addSpectrumProcessor();

  Localized<String> addSpectrumProcessorDescription();

  Localized<String> removeExperimentDialog();

  Localized<String> removeExperimentText(WorkspaceExperiment experiment);

  Localized<String> removeExperimentConfirmation();

  Localized<String> removeExperimentFailedDialog();

  Localized<String> removeExperimentFailedText(WorkspaceExperiment experiment);

  Localized<String> removeExperimentFailedDescription();

  Localized<String> removeStepFailedDialog();

  Localized<String> removeStepFailedText(Step step);

  Localized<String> removeStepFailedDescription();

  Localized<String> renameExperimentFailedDialog();

  Localized<String> renameExperimentFailedText(WorkspaceExperiment experiment);

  Localized<String> renameExperimentFailedDescription();

  Localized<String> newExperimentFailedDialog();

  Localized<String> newExperimentFailedText(Workspace workspace, String descriptor);

  Localized<String> newExperimentFailedDescription();

  Localized<String> openExperimentFailedDialog();

  Localized<String> openExperimentFailedText(WorkspaceExperiment experiment);

  Localized<String> openExperimentFailedDescription();

  Localized<String> attachStepFailedDialog();

  Localized<String> attachStepFailedText(WorkspaceExperiment experiment, String name);

  Localized<String> attachStepFailedText(Step step, String name);

  Localized<String> attachStepFailedDescription();

  Localized<String> newWorkspaceExperiment();
}
