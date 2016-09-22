/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import uk.co.saiman.SaiProperties;
import uk.co.strangeskies.text.properties.Localized;
import uk.co.strangeskies.text.properties.Properties;
import uk.co.strangeskies.text.properties.PropertyConfiguration;
import uk.co.strangeskies.text.properties.PropertyConfiguration.KeyCase;

@PropertyConfiguration(keyCase = KeyCase.LOWER, keySplitString = ".")
public interface ExperimentExceptionProperties extends Properties<ExperimentExceptionProperties> {
	SaiProperties sai();

	/**
	 * @param descendantType
	 *          the type of the descendant we wish to add
	 * @param ancestorNode
	 *          an ancestor of the candidate node
	 * @return a node of the given type may not be a descendant of the given node
	 */
	Localized<String> typeMayNotSucceed(ExperimentType<?> descendantType, ExperimentNode<?, ?> ancestorNode);

	Localized<String> experimentIsDisposed(ExperimentNode<?, ?> experimentNode);

	Localized<String> illegalCommandForSelection(String commandId, Object selection);

	Localized<String> illegalMenuForSelection(String commandId, Object selection);

	Localized<String> experimentDoesNotExist(ExperimentNode<?, ?> experimentNode);

	Localized<String> invalidExperimentName(String name);

	Localized<String> cannotProcessExperimentConcurrently(ExperimentNode<RootExperiment, ExperimentConfiguration> root);
}
