/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.util.Optional;
import java.util.function.Supplier;

import uk.co.saiman.experiment.Result;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.reflection.token.TypeToken;

public class ResultImpl<T> implements Result<T> {
  private final ExperimentNodeImpl<?, T> node;
  private Supplier<T> valueSupplier;
  private T value;

  private boolean dirty;
  private final HotObservable<Result<T>> updates;

  public ResultImpl(ExperimentNodeImpl<?, T> node) {
    this.node = node;
    this.updates = new HotObservable<>();
  }

  @Override
  public ExperimentNodeImpl<?, ?> getExperimentNode() {
    return node;
  }

  @Override
  public TypeToken<T> getType() {
    return node.getType().getResultType();
  }

  private void update() {
    if (!dirty) {
      dirty = true;
      updates.next(this);
    }
  }

  void setValue(T value) {
    synchronized (updates) {
      this.value = value;
      update();
    }
  }

  void unsetValue() {
    synchronized (updates) {
      value = null;
      update();
    }
  }

  void setValueSupplier(Supplier<T> valueSupplier) {
    synchronized (updates) {
      this.valueSupplier = valueSupplier;
      update();
    }
  }

  @Override
  public Optional<T> getValue() {
    synchronized (updates) {
      dirty = false;
      if (valueSupplier != null) {
        value = valueSupplier.get();
        valueSupplier = null;
      }
      return Optional.ofNullable(value);
    }
  }

  @Override
  public Observable<Result<T>> updates() {
    return updates;
  }
}
