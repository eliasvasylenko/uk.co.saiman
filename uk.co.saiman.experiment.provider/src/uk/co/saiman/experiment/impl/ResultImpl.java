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

import java.util.Optional;

import uk.co.saiman.experiment.Result;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Invalidation;
import uk.co.saiman.observable.MissingValueException;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.reflection.token.TypeToken;

public class ResultImpl<T> implements Result<T> {
  private final ExperimentNodeImpl<?, T> node;
  private T value;

  private final HotObservable<Invalidation<T>> invalidations;
  private Invalidation<T> invalidation;

  public ResultImpl(ExperimentNodeImpl<?, T> node) {
    this.node = node;
    this.invalidations = new HotObservable<>();
  }

  @Override
  public ExperimentNodeImpl<?, ?> getExperimentNode() {
    return node;
  }

  @Override
  public TypeToken<T> getType() {
    return node.getType().getResultType();
  }

  void setValue(T value) {
    this.value = value;
    invalidation = null;
    invalidations.next(new Invalidation<T>() {
      @Override
      public T revalidate() {
        return value;
      }
    });
  }

  void unsetValue() {
    value = null;
    invalidation = null;
    invalidations.next(new Invalidation<T>() {
      @Override
      public T revalidate() {
        throw new MissingValueException(new NullPointerException());
      }
    });
  }

  void setInvalidation(Invalidation<T> invalidation) {
    this.invalidation = invalidation;
    invalidations.next(invalidation);
  }

  @Override
  public Optional<T> getValue() {
    if (invalidation != null) {
      try {
        setValue(invalidation.revalidate());
      } catch (MissingValueException e) {
        unsetValue();
      }
      invalidation = null;
    }

    return Optional.ofNullable(value);
  }

  @Override
  public Observable<Invalidation<T>> invalidations() {
    return invalidations.invalidateLazyRevalidate().map(i -> i.map(Invalidation::revalidate));
  }
}
