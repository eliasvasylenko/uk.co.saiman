/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Component;

import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

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
	 * @return The current configuration
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
	 * Partially update the configuration.
	 * 
	 * @param partialConfiguration
	 *          A superinterface of the configuration type, such that its members
	 *          can be partially applied to the configuration of this configurable
	 *          instance
	 */
	@SuppressWarnings("unchecked")
	default void updateConfiguration(Supplier<? super C> partialConfiguration) {
		Object partial = partialConfiguration.get();
		C overridden = configuration();

		configure((C) Proxy.newProxyInstance(getClass().getClassLoader(),
				getConfigurationType().getRawTypes().toArray(new Class<?>[getConfigurationType().getRawTypes().size()]),
				(Object proxy, Method method, Object[] args) -> {
					if (method.getDeclaringClass().isAssignableFrom(proxy.getClass())) {
						return method.invoke(partial, args);
					} else {
						return method.invoke(overridden, args);
					}
				}));
	}

	/**
	 * Partially update a configuration.
	 * 
	 * @param configurable
	 *          A configurable to partially update
	 * @param configurationOverride
	 *          A superinterface of the configuration type, such that its members
	 *          can be partially applied to the configuration of the given
	 *          configurable instance
	 */
	static <S> void updateConfiguration(Configurable<? extends S> configurable, S configurationOverride) {
		configurable.updateConfiguration((Supplier<S>) () -> configurationOverride);
	}

	/**
	 * @return The exact generic type of the configuration interface
	 */
	default TypeToken<C> getConfigurationType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(Configurable.class)
				.resolveTypeArgument(new TypeParameter<C>() {}).infer();
	}
}
