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

import uk.co.strangeskies.reflection.Reified;
import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

/**
 * This class does not actually contain any result data, and instances are
 * associated with {@link ExperimentType experiment types} not
 * {@link ExperimentNode experiment nodes}. Therefore rather than being a
 * description of the result data itself, it is a description of how result data
 * is obtained from the experiment state/configuration.
 * 
 * @author Elias N Vasylenko
 *
 * @param <S>
 *          the type of the experiment state
 * @param <T>
 *          the type of the result data
 */
public class ExperimentResultType<S, T> implements Reified {
	private final String name;
	private final ExperimentType<S> experimentType;
	private final TypeToken<T> dataType;

	public ExperimentResultType(String name, ExperimentType<S> experimentType, TypeToken<T> dataType) {
		this.name = name;
		this.experimentType = experimentType;
		this.dataType = dataType;
	}

	public String getName() {
		return name;
	}

	public ExperimentType<S> getExperimentType() {
		return experimentType;
	}

	public TypeToken<T> getDataType() {
		return dataType;
	}

	@Override
	public TypeToken<ExperimentResultType<S, T>> getThisType() {
		return new TypeToken<ExperimentResultType<S, T>>() {}.withTypeArgument(new TypeParameter<T>() {}, getDataType())
				.withTypeArgument(new TypeParameter<S>() {}, getExperimentType().getStateType());
	}
}
