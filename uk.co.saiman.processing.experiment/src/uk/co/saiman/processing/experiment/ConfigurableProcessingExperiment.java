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
public interface ConfigurableProcessingExperiment<C, T, R> extends ExperimentType<C, T, R> {
	ConfigurableProcessor<C, T, R> getConfigurableProcessor();

	static <T, R, C> ConfigurableProcessingExperiment<T, R, C> over(ConfigurableProcessor<T, R, C> processor) {
		return new ConfigurableProcessingExperiment<T, R, C>() {
			@Override
			public ConfigurableProcessor<T, R, C> getConfigurableProcessor() {
				return processor;
			}

			@Override
			public void validate(T configuration) {
				processor.configure(configuration);
			}

			@Override
			public C process(T configuration, R input) {
				return processor.configure(configuration).process(input);
			}
		};
	}
}
