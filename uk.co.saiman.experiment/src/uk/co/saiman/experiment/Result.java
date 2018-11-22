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
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.reflection.token.TypeArgument;
import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

/**
 * TODO this should probably be weak-referenced to the actual data object, with
 * a way to load (and save?) according to whatever {@link DataFormat format} it
 * was originally saved as or is appropriate. Otherwise there will certainly be
 * memory usage issues. So how can this work alongside the observable value
 * aspect? Presumably if any observers are attached this will count as a
 * reference to the data object? Or will they just be sent a fail event after
 * it's cleaned up by GC?
 * 
 * TODO also consider that the data should still be loadable even if the
 * ExperimentNode type is {@link UnknownProcedure missing}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the data type of the result
 */
public class Result<T> {
  private final Procedure<?, T> procedure;
  private Supplier<T> valueSupplier;
  private T value;
  private Data<T> resultData;

  private boolean dirty;
  private final HotObservable<Result<T>> updates;

  public Result(Procedure<?, T> procedure) {
    this.procedure = procedure;
    this.updates = new HotObservable<>();
  }

  public Procedure<?, T> getProcedure() {
    return procedure;
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
      this.value = value;
      this.resultData = data;
      update();
    }
  }

  void setValue(T value) {
    synchronized (updates) {
      this.value = value;
      if (resultData != null) {
        resultData.set(value);
      }
      update();
    }
  }

  void unsetValue() {
    synchronized (updates) {
      value = null;
      if (resultData != null) {
        resultData.unset();
        resultData.save();
        resultData = null;
      }
      update();
    }
  }

  void setValueSupplier(Supplier<T> valueSupplier) {
    synchronized (updates) {
      this.valueSupplier = valueSupplier;
      update();
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

  public TypeToken<T> getType() {
    return procedure.getResultType();
  }

  public TypeToken<Result<T>> getThisTypeToken() {
    return new TypeToken<Result<T>>() {}.withTypeArguments(new TypeArgument<T>(getType()) {});
  }

  public TypedReference<Result<T>> asTypedObject() {
    return TypedReference.typedObject(getThisTypeToken(), this);
  }
}
