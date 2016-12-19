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
 * This file is part of uk.co.saiman.utilities.
 *
 * uk.co.saiman.utilities is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.utilities is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.utilities;

import org.osgi.service.component.annotations.Component;

import uk.co.strangeskies.reflection.token.TypeParameter;
import uk.co.strangeskies.reflection.token.TypeToken;

/**
 * Implementation of this API designates a class which is configurable by a
 * given configuration interface type.
 * 
 * @author Elias N Vasylenko
 *
 * @param <C>
 *          The configuration interface type
 */
@Component
public interface Configurable<C> {
	/**
	 * @return The current configuration object.
	 */
	C configuration();

	/**
	 * Update the configuration.
	 * 
	 * @param configuration
	 *          The new configuration to adopt
	 */
	void configure(C configuration);

	/**
	 * @return The exact generic type of the configuration interface
	 */
	default TypeToken<C> getConfigurationType() {
		return TypeToken
				.overType(getClass())
				.resolveSupertype(Configurable.class)
				.resolveTypeArgument(new TypeParameter<C>() {})
				.infer();
	}
}
