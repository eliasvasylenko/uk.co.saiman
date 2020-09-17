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
 * This file is part of uk.co.saiman.observable.
 *
 * uk.co.saiman.observable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.observable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.observable;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class AssertingObserver<T> extends PassthroughObserver<T, T> {
  private final Function<? super T, ? extends Optional<? extends Supplier<? extends Throwable>>> mapping;

  public AssertingObserver(
      Observer<? super T> downstreamObserver,
      Function<? super T, ? extends Optional<? extends Supplier<? extends Throwable>>> mapping) {
    super(downstreamObserver);

    this.mapping = requireNonNull(mapping);
  }

  @Override
  public void onNext(T message) {
    mapping.apply(message).map(failure -> (Runnable) () -> {
      getObservation().cancel();
      getDownstreamObserver().onFail(failure.get());
    }).orElseGet(() -> () -> getDownstreamObserver().onNext(message)).run();
  }
}
