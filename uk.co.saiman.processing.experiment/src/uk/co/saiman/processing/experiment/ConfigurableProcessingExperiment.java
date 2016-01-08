/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.processing.experiment.
 *
 * uk.co.saiman.processing.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.processing.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.processing.experiment;

import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.processing.ConfigurableProcessor;
import uk.co.strangeskies.reflection.TypeToken;

/**
 * A configurable processor accepts an instance of a specified configuration
 * model type. The type of the configuration should typically be an interface,
 * such that implementation and persistence of configuration can more
 * 
 * @author Elias N Vasylenko
 * 
 * @param <C>
 *          The type of the configuration interface.
 * @param <T>
 *          The type of the experiment processing target.
 * @param <R>
 *          The type of the experiment processing result.
 */
public interface ConfigurableProcessingExperiment<C, T, R> extends ExperimentType<C, T, R> {
	ConfigurableProcessor<C, T, R> getConfigurableProcessor();

	static <C, T, R> ConfigurableProcessingExperiment<C, T, R> over(ConfigurableProcessor<C, T, R> processor) {
		return new ConfigurableProcessingExperiment<C, T, R>() {
			@Override
			public ConfigurableProcessor<C, T, R> getConfigurableProcessor() {
				return processor;
			}

			@Override
			public void validate(C configuration) {
				processor.configure(configuration);
			}

			@Override
			public R execute(C configuration, T input) {
				return processor.configure(configuration).process(input);
			}
		};
	}

	@Override
	default TypeToken<C> getConfigurationType() {
		return getConfigurableProcessor().getConfigurationType();
	}

	@Override
	default TypeToken<T> getInputType() {
		return getConfigurableProcessor().getTargetType();
	}

	@Override
	default TypeToken<R> getOutputType() {
		return getConfigurableProcessor().getResultType();
	}
}
