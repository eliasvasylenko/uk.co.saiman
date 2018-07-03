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
package uk.co.saiman.experiment;

import java.nio.file.Path;

import uk.co.saiman.properties.LocalizedString;
import uk.co.saiman.properties.Nested;
import uk.co.saiman.properties.SaiProperties;

/**
 * Properties interface for texts relating to experiments.
 * 
 * @author Elias N Vasylenko
 */
@SuppressWarnings("javadoc")
public interface ExperimentProperties {
  @Nested
  SaiProperties sai();

  LocalizedString newExperiment();

  LocalizedString newExperimentName();

  /**
   * @param state
   *          the state to localize
   * @return localized name of the state
   */
  default LocalizedString lifecycleState(ExperimentLifecycleState state) {
    switch (state) {
    case COMPLETION:
      return lifecycleStateCompletion();
    case CONFIGURATION:
      return lifecycleStateConfiguration();
    case DISPOSED:
      return lifecycleStateDisposed();
    case FAILURE:
      return lifecycleStatefailure();
    case PREPARATION:
      return lifecycleStatePreparation();
    case PROCESSING:
      return lifecycleStateProcessing();
    case WAITING:
      return lifecycleStateWaiting();
    }
    throw new AssertionError();
  }

  LocalizedString lifecycleStateCompletion();

  LocalizedString lifecycleStateConfiguration();

  LocalizedString lifecycleStateDisposed();

  LocalizedString lifecycleStatefailure();

  LocalizedString lifecycleStatePreparation();

  LocalizedString lifecycleStateProcessing();

  LocalizedString lifecycleStateWaiting();

  LocalizedString configuration();

  LocalizedString missingResult();

  LocalizedString missingExperimentType(String id);

  LocalizedString experimentRoot();

  LocalizedString overwriteData();

  LocalizedString overwriteDataConfirmation(Path newLocation);

  LocalizedString renameExperiment();

  LocalizedString renameExperimentName(String name);

  LocalizedString cannotCreateWorkspace(Workspace experimentWorkspace);

  LocalizedString addSpectrumProcessor();

  LocalizedString addSpectrumProcessorDescription();
}
