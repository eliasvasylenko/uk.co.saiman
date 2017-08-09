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
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.ResultType;
import uk.co.saiman.observable.HotObservable;

public class ResultImpl<T> extends HotObservable<Optional<T>> implements Result<T> {
  private final ExperimentNodeImpl<?, ?> node;
  private final ResultType<T> resultType;
  private T data;

  public ResultImpl(ExperimentNodeImpl<?, ?> node, ResultType<T> type) {
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
  public ResultType<T> getResultType() {
    return resultType;
  }

  protected void setData(T data) {
    this.data = data;
    next(getData());
  }

  @Override
  public Optional<T> getData() {
    return Optional.ofNullable(data);
  }

  @Override
  public Result<T> copy() {
    return this;
  }
}
