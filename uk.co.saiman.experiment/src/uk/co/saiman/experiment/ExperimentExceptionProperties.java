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

import uk.co.saiman.text.properties.Nested;
import uk.co.saiman.text.properties.PropertyConfiguration;
import uk.co.saiman.text.properties.PropertyConfiguration.KeyCase;
import uk.co.saiman.text.properties.SaiProperties;

@PropertyConfiguration(keyCase = KeyCase.LOWER, keySplitString = ".")
public interface ExperimentExceptionProperties {
  @Nested
  SaiProperties sai();

  String experimentInterrupted();

  /**
   * @param descendantType
   *          the type of the descendant we wish to add
   * @param ancestorNode
   *          an ancestor of the candidate node
   * @return a node of the given type may not be a descendant of the given node
   */
  String typeMayNotSucceed(ExperimentType<?, ?> descendantType, ExperimentNode<?, ?> ancestorNode);

  String experimentIsDisposed(ExperimentNode<?, ?> experimentNode);

  String illegalMenuForSelection(String commandId, Object selection);

  String experimentDoesNotExist(String id);

  String invalidExperimentName(String name);

  String cannotProcessExperimentConcurrently(Experiment experiment);

  String cannotPersistState(Experiment experiment);

  String cannotCreateWorkspace(Workspace experimentWorkspace);

  String cannotLoadExperiment(String id, String typeId);

  String cannotLoadExperiments();

  String duplicateExperimentName(String name);

  String cannotExecuteMissingExperimentType(String id);

  String cannotMove(Path oldLocation, Path newLocation);

  String cannotCreate(Path newLocation);

  String dataAlreadyExists(Path newLocation);

  String failedExperimentExecution(ExperimentNode<?, ?> experimentNode);

  String userCancelledSetExperimentName();

  String cannotDelete(Path newLocation);

  String cannotRemoveExperiment(ExperimentNode<?, ?> experiment);

  String cannotOpenResultEditor();

  String cannotResolveContainingExperimentNode(Workspace experimentWorkspace, Path path);

  String cannotResolveExperimentNode(Workspace experimentWorkspace, Path path);

  String cannotResolveExperimentResult(Workspace experimentWorkspace, Path path);

  String experimentAlreadyExists(String name);

  String cannotRenameExperiment(ExperimentNode<?, ?> experimentNode, String id);

  String cannotPrepareLocation(ExperimentNode<?, ?> experimentNode);

  String cannotCreateExperiment(ExperimentNode<?, ?> parent);

  String cannotCreateWorkspace(Path workspaceRoot);

  String cannotCopyFromOutsideWorkspace();

  String cannotMoveExperiment(ExperimentNode<?, ?> experimentNode);
}
