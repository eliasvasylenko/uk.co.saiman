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
import uk.co.saiman.processing.Processor;
import uk.co.strangeskies.reflection.TypeToken;

/**
 * An experiment type which will apply a {@link Processor} to the result of a
 * parent experiment part, and output the result.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          The type of the experiment input
 * @param <R>
 *          The type of the experiment output
 */
public interface ProcessingExperiment<T, R> extends ExperimentType<Void, T, R> {
	/**
	 * @return The processor to be applied via experiment execution
	 */
	Processor<T, R> getProcessor();

	/**
	 * @param processor
	 *          The processor to be applied via experiment execution
	 * @return A {@link ProcessingExperiment} over the given {@link Processor}
	 */
	static <T, R> ProcessingExperiment<T, R> over(Processor<T, R> processor) {
		return new ProcessingExperiment<T, R>() {
			@Override
			public Processor<T, R> getProcessor() {
				return processor;
			}

			@Override
			public void validate(Void configuration) {}

			@Override
			public R execute(Void configuration, T input) {
				return processor.process(input);
			}
		};
	}

	@Override
	default TypeToken<T> getInputType() {
		return getProcessor().getTargetType();
	}

	@Override
	default TypeToken<R> getOutputType() {
		return getProcessor().getResultType();
	}
}
