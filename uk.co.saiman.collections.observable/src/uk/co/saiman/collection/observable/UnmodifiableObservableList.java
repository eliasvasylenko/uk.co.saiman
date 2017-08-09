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
 * This file is part of uk.co.saiman.collections.observable.
 *
 * uk.co.saiman.collections.observable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.collections.observable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.collection.observable;

import java.util.Collections;
import java.util.List;

import uk.co.saiman.collection.ListDecorator;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.Observer;

public abstract class UnmodifiableObservableList<S extends ObservableList<S, E>, E>
    implements ListDecorator<E>, ObservableList<S, E> {
  static class UnmodifiableObservableListImpl<E>
      extends UnmodifiableObservableList<UnmodifiableObservableListImpl<E>, E> {
    UnmodifiableObservableListImpl(ObservableList<?, ? extends E> component) {
      super(component);
    }

    @SuppressWarnings("unchecked")
    @Override
    public UnmodifiableObservableListImpl<E> copy() {
      return new UnmodifiableObservableListImpl<>(((ObservableList<?, E>) getComponent()).copy());
    }
  }

  private final List<E> component;

  private final Observable<S> observable;
  private final Observable<Change<E>> changes;

  @SuppressWarnings("unchecked")
  protected UnmodifiableObservableList(ObservableList<?, ? extends E> component) {
    this.component = Collections.unmodifiableList(component);

    observable = component.weakReference(this).map(m -> m.owner().getThis());
    changes = component.changes().weakReference(this).map(m -> (Change<E>) m.message());
  }

  @Override
  public List<E> getComponent() {
    return component;
  }

  @Override
  public Disposable observe(Observer<? super S> observer) {
    return observable.observe(observer);
  }

  @Override
  public Observable<Change<E>> changes() {
    return changes;
  }

  @Override
  public List<E> silent() {
    return component;
  }

  @Override
  public String toString() {
    return getComponent().toString();
  }

  @Override
  public int hashCode() {
    return getComponent().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return getComponent().equals(obj);
  }
}
