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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import uk.co.strangeskies.observable.Observable;
import uk.co.strangeskies.observable.Observation;

public class CachingObservableResource<T> extends CachingResource<T> {
  private final Function<? super T, ? extends Observable<?>> observable;
  private Observation observation;

  public CachingObservableResource(
      Supplier<T> load,
      Consumer<T> save,
      Function<? super T, ? extends Observable<?>> observable) {
    super(load, save);

    this.observable = observable;
  }

  private void addObserver(T data) {
    observation = observable.apply(data).observe((Object o) -> makeDirty());
  }

  @Override
  public boolean save() {
    boolean saved = super.save();

    if (saved) {
      addObserver(getData());
    }

    return saved;
  }

  /**
   * Mark the data as inconsistent with the previously saved state.
   */
  @Override
  public boolean makeDirty() {
    boolean madeDirty = super.makeDirty();

    if (observation != null) {
      observation.dispose();
    }

    return madeDirty;
  }

  @Override
  public boolean setData(T data) {
    boolean dataSet = super.setData(data);

    if (dataSet) {
      if (observation != null)
        observation.dispose();
      addObserver(getData());
    }

    return dataSet;
  }

  /**
   * Get the data, loading from the input channel if necessary.
   * 
   * @return the data
   */
  @Override
  public T getData() {
    T data = super.getData();

    addObserver(data);

    return data;
  }
}
