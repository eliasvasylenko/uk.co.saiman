/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
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
import uk.co.strangeskies.text.properties.Properties;
import uk.co.strangeskies.text.properties.PropertyConfiguration;
import uk.co.strangeskies.text.properties.PropertyConfiguration.KeyCase;

/**
 * {@link Properties} interface for texts relating to experiments.
 * 
 * @author Elias N Vasylenko
 */
@SuppressWarnings("javadoc")
@PropertyConfiguration(keyCase = KeyCase.LOWER, keySplitString = ".")
public interface ExperimentProperties extends Properties<ExperimentProperties> {
	SaiProperties sai();

	Localized<String> newExperiment();

	Localized<String> newExperimentName();

	/**
	 * @param state
	 *          the state to localize
	 * @return localized name of the state
	 */
	default Localized<String> lifecycleState(ExperimentLifecycleState state) {
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

	Localized<String> lifecycleStateCompletion();

	Localized<String> lifecycleStateConfiguration();

	Localized<String> lifecycleStateDisposed();

	Localized<String> lifecycleStatefailure();

	Localized<String> lifecycleStatePreparation();

	Localized<String> lifecycleStateProcessing();

	Localized<String> lifecycleStateWaiting();

	ExperimentExceptionProperties exception();

	Localized<String> configuration();

	Localized<String> missingResult();

	Localized<String> invalidExperimentName(String name);

	Localized<String> duplicateExperimentName(String name);

	Localized<String> experimentRoot();

	Localized<String> cannotDelete(Path newLocation);

	Localized<String> cannotMove(Path oldLocation, Path newLocation);

	Localized<String> cannotCreate(Path newLocation);

	Localized<String> overwriteData();

	Localized<String> overwriteDataConfirmation(Path newLocation);

	Localized<String> userCancelledSetExperimentName();

	Localized<String> dataAlreadyExists(Path newLocation);

	Localized<String> renameExperiment();

	Localized<String> renameExperimentName(String name);
}
