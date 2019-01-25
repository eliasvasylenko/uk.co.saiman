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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment;

import java.util.Optional;
import java.util.function.Supplier;

import uk.co.saiman.data.Data;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

/**
 * A result which may be produced during the processing of an
 * {@link ExperimentStep experiment step}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the data type of the result
 */
public class Result<T> extends Resource {
  private final Observation<T> observation;

  private Supplier<? extends T> valueSupplier;
  private T value;
  private Data<T> resultData;

  private boolean complete;
  private boolean dirty;
  private final HotObservable<Result<T>> updates;

  Result(ExperimentStep<?> step, Observation<T> observation) {
    super(step);
    this.observation = observation;
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

  public boolean isComplete() {
    synchronized (updates) {
      return complete;
    }
  }

  public boolean isPartiallyComplete() {
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
  public Observable<Result<T>> updates() {
    return updates;
  }
}
