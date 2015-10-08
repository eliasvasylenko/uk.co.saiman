/*
 * Copyright (C) 2015 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.processing.
 *
 * uk.co.saiman.processing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.processing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.processing;

import java.util.HashSet;
import java.util.Set;

/**
 * A configurable processor accepts an instance of a specified configuration
 * model type. The type of the configuration should typically be an interface,
 * such that implementation and persistence of configuration can more
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 * @param <R>
 * @param <C>
 */
public interface ConfigurableProcessStack<T, R, C> extends ProcessStack<T, R> {
	ConfigurableProcessor<T, R, C> getConfigurableProcessor();

	default Processor<T, R> getProcessor() {
		return getConfigurableProcessor().configure(getConfiguration());
	}

	C getConfiguration();

	void setConfiguration(C configuration);

	static <T, R, C> ConfigurableProcessStack<T, R, C> over(ConfigurableProcessor<T, R, C> processor) {
		return new ConfigurableProcessStack<T, R, C>() {
			private final Set<ProcessStack<R, ?>> children = new HashSet<>();

			private T target;
			private R result;
			private C configuration;

			public void setTarget(T target) {
				this.target = target;
				result = getProcessor().process(target);
				for (ProcessStack<R, ?> child : children)
					child.setTarget(result);
			}

			public Processor<T, R> getProcessor() {
				return processor.configure(getConfiguration());
			}

			public Set<ProcessStack<R, ?>> children() {
				return children;
			}

			public R getResult() {
				return result;
			}

			public T getTarget() {
				return target;
			}

			@Override
			public ConfigurableProcessor<T, R, C> getConfigurableProcessor() {
				return processor;
			}

			@Override
			public C getConfiguration() {
				return configuration;
			}

			@Override
			public void setConfiguration(C configuration) {
				this.configuration = configuration;
			}
		};
	}

	static <T, R, C> ConfigurableProcessStack<T, R, C> over(ConfigurableProcessor<T, R, C> processor, T target) {
		ConfigurableProcessStack<T, R, C> stack = over(processor);
		stack.setTarget(target);
		return stack;
	}
}
