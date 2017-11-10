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
 * This file is part of uk.co.saiman.data.
 *
 * uk.co.saiman.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data;

import java.util.function.Function;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.Observable;

/**
 * Sometimes a data object is mutable, in which case ideally we would like to
 * automatically invalidate the {@link Data container} upon modification. This
 * class enables this functionality by allowing the user to attach an observer
 * to the data.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of the data object
 */
public class ObservableData<T> implements Data<T> {
  private final Data<T> backingData;
  private final Function<? super T, ? extends Observable<?>> observable;
  private Disposable observation;

  public ObservableData(
      Data<T> backingData,
      Function<? super T, ? extends Observable<?>> observable) {
    this.backingData = backingData;
    this.observable = observable;
  }

  private void addObserver(T data) {
    observation = observable.apply(data).observe((Object o) -> makeDirty());
  }

  @Override
  public boolean save() {
    boolean saved = backingData.save();

    if (saved) {
      addObserver(get());
    }

    return saved;
  }

  @Override
  public boolean load() {
    return backingData.load();
  }

  /**
   * Mark the data as inconsistent with the previously saved state.
   */
  @Override
  public void makeDirty() {
    backingData.makeDirty();

    if (observation != null) {
      observation.cancel();
    }
  }

  @Override
  public boolean set(T data) {
    boolean dataSet = backingData.set(data);

    if (dataSet) {
      if (observation != null)
        observation.cancel();
      addObserver(data);
    }

    return dataSet;
  }

  /**
   * Get the data, loading from the input channel if necessary.
   * 
   * @return the data
   */
  @Override
  public T get() {
    T data = backingData.get();

    addObserver(data);

    return data;
  }

  @Override
  public Resource getResource() {
    return backingData.getResource();
  }

  @Override
  public DataFormat<T> getFormat() {
    return backingData.getFormat();
  }

  @Override
  public boolean isDirty() {
    return backingData.isDirty();
  }
}
