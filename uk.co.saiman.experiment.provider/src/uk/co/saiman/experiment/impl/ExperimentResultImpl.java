/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.impl;

import java.nio.file.Path;
import java.util.Optional;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentResult;
import uk.co.saiman.experiment.ExperimentResultType;
import uk.co.strangeskies.observable.ObservableImpl;

public class ExperimentResultImpl<T> extends ObservableImpl<Optional<T>>
		implements ExperimentResult<T> {
	private final ExperimentNodeImpl<?, ?> node;
	private final ExperimentResultType<T> resultType;
	private T data;

	public ExperimentResultImpl(ExperimentNodeImpl<?, ?> node, ExperimentResultType<T> type) {
		this.node = node;
		this.resultType = type;
	}

	@Override
	public Path getResultDataPath() {
		return node.getResultDataPath();
	}

	@Override
	public ExperimentNode<?, ?> getExperimentNode() {
		return node;
	}

	@Override
	public ExperimentResultType<T> getResultType() {
		return resultType;
	}

	protected void setData(T data) {
		this.data = data;
		fire(getData());
	}

	@Override
	public Optional<T> getData() {
		return Optional.ofNullable(data);
	}

	@Override
	public ExperimentResult<T> copy() {
		return this;
	}
}
