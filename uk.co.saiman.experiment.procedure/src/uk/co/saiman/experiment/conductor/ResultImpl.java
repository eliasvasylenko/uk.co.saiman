/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.procedure.
 *
 * uk.co.saiman.experiment.procedure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.procedure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.conductor;

import java.util.Optional;
import java.util.function.Supplier;

import uk.co.saiman.data.Data;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.dependency.source.Observation;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

/**
 * A result which may be produced during the processing of an experiment step.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the data type of the result
 */
public class ResultImpl<T> implements Result<T> {
  private final Observation<T> observation;
  private final ProductPath<Absolute, Result<? extends T>> path;

  private Supplier<? extends T> valueSupplier;
  private T value;
  private Data<T> resultData;

  private boolean complete;
  private boolean dirty;
  private final HotObservable<Result<T>> updates;

  ResultImpl(Observation<T> observation, ExperimentPath<Absolute> experimentPath) {
    this.observation = observation;
    this.path = ProductPath.define(experimentPath, observation);
    this.updates = new HotObservable<>();
  }

  public Observation<T> getObservation() {
    return observation;
  }

  private void update() {
    if (!dirty) {
      dirty = true;
      updates.next(this);
    }
  }

  void setData(Data<T> data) {
    T value = data.get();

    synchronized (updates) {
      complete = false;
      this.value = value;
      this.resultData = data;
      update();
    }
  }

  void setValue(T value) {
    synchronized (updates) {
      complete = true;
      this.value = value;
      if (resultData != null) {
        resultData.set(value);
      }
      update();
    }
  }

  public void complete() {
    synchronized (updates) {
      complete = true;
      update();
    }
  }

  void unsetValue() {
    synchronized (updates) {
      complete = false;
      value = null;
      if (resultData != null) {
        resultData.unset();
        resultData.save();
        resultData = null;
      }
      update();
    }
  }

  void setPartialValue(Supplier<? extends T> valueSupplier) {
    synchronized (updates) {
      complete = false;
      this.valueSupplier = valueSupplier;
      update();
    }
  }

  @Override
  public boolean isComplete() {
    synchronized (updates) {
      return complete;
    }
  }

  @Override
  public boolean isPartial() {
    synchronized (updates) {
      return !isComplete() && value == null && valueSupplier == null;
    }
  }

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

  /**
   * Observe updates to the result value. Update events are sent with
   * invalidate/lazy-revalidate semantics. This means that once an update has been
   * sent, further updates are withheld until the previous change has actually
   * been {@link #getValue() observed}. This means that consumers can deal with
   * changes in their own time, and publishers may have the option to skip
   * processing and memory allocation for updates which are not consumed.
   * 
   * @return an observable over update events
   */
  @Override
  public Observable<Result<T>> updates() {
    return updates;
  }

  @Override
  public Optional<T> value() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Observation<T> source() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ProductPath<Absolute, Result<? extends T>> path() {
    return path;
  }

  @Override
  public boolean isEmpty() {
    // TODO Auto-generated method stub
    return false;
  }
}
