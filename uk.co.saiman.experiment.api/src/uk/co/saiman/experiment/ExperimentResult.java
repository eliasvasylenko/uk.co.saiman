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

import java.util.List;

import uk.co.saiman.processing.Processor;
import uk.co.strangeskies.reflection.Reified;
import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

/**
 * Experiment results can be automatically picked up from an experiment's modabi
 * model to be dealt with by GUI items such as the experiment tree.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of the result data
 */
public class ExperimentResult<T> implements Reified<ExperimentResult<T>> {
	private final TypeToken<T> dataType;
	private T data;

	private List<Processor<T, ?>> processingItems;

	public ExperimentResult(TypeToken<T> dataType) {
		this.dataType = dataType;
	}

	public List<Processor<T, ?>> processingItems() {
		return processingItems;
	}

	public boolean isResultObtained() {
		return data != null;
	}

	public T getResultData() {
		return data;
	}

	public void setResultData(T data) {
		this.data = data;
	}

	@Override
	public ExperimentResult<T> copy() {
		ExperimentResult<T> copy = new ExperimentResult<>(dataType);
		copy.setResultData(data);
		return copy;
	}

	public TypeToken<T> getDataType() {
		return dataType;
	}

	@Override
	public TypeToken<ExperimentResult<T>> getThisType() {
		return new TypeToken<ExperimentResult<T>>() {}.withTypeArgument(new TypeParameter<T>() {}, dataType);
	}
}
