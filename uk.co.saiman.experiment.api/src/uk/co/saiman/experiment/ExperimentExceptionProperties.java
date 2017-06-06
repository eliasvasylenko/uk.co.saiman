/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.api.
 *
 * uk.co.saiman.experiment.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment;

import java.nio.file.Path;

import uk.co.saiman.SaiProperties;
import uk.co.strangeskies.text.properties.Localized;
import uk.co.strangeskies.text.properties.Nested;
import uk.co.strangeskies.text.properties.PropertyConfiguration;
import uk.co.strangeskies.text.properties.PropertyConfiguration.KeyCase;

@PropertyConfiguration(keyCase = KeyCase.LOWER, keySplitString = ".")
public interface ExperimentExceptionProperties {
	@Nested
	SaiProperties sai();

	/**
	 * @param descendantType
	 *          the type of the descendant we wish to add
	 * @param ancestorNode
	 *          an ancestor of the candidate node
	 * @return a node of the given type may not be a descendant of the given node
	 */
	Localized<String> typeMayNotSucceed(
			ExperimentType<?> descendantType,
			ExperimentNode<?, ?> ancestorNode);

	Localized<String> experimentIsDisposed(ExperimentNode<?, ?> experimentNode);

	Localized<String> illegalCommandForSelection(String commandId, Object selection);

	Localized<String> illegalMenuForSelection(String commandId, Object selection);

	Localized<String> experimentDoesNotExist(ExperimentNode<?, ?> experimentNode);

	Localized<String> invalidExperimentName(String name);

	Localized<String> cannotProcessExperimentConcurrently(Experiment experiment);

	Localized<String> cannotPersistState(Experiment experiment);

	Localized<String> cannotCreateWorkspace(Workspace experimentWorkspace);

	Localized<String> cannotLoadExperiment(Path path);
}
