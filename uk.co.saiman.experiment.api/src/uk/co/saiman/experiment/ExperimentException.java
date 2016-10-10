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

import java.util.function.Function;

import uk.co.strangeskies.text.properties.Localized;
import uk.co.strangeskies.text.properties.LocalizedRuntimeException;
import uk.co.strangeskies.text.properties.PropertyLoader;

/**
 * A problem with experiment configuration or processing.
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentException extends LocalizedRuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 *          a function from a localized text interface to a {@link Localized}
	 * @param cause
	 *          the exception cause
	 */
	public ExperimentException(Function<ExperimentProperties, Localized<String>> message, Throwable cause) {
		super(message.apply(getText()), cause);
	}

	/**
	 * @param message
	 *          a function from a localized text interface to a {@link Localized}
	 */
	public ExperimentException(Function<ExperimentProperties, Localized<String>> message) {
		this(message, null);
	}

	/**
	 * @param message
	 *          a {@link Localized} describing the exception
	 * @param cause
	 *          the exception cause
	 */
	public ExperimentException(Localized<String> message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *          a {@link Localized} describing the exception
	 */
	public ExperimentException(Localized<String> message) {
		this(message, null);
	}

	protected static ExperimentProperties getText() {
		return PropertyLoader.getDefaultPropertyLoader().getProperties(ExperimentProperties.class);
	}
}
