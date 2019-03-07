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
package uk.co.saiman.experiment.procedure;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import uk.co.saiman.experiment.product.Condition;
import uk.co.saiman.experiment.product.Preparation;
import uk.co.saiman.experiment.product.Production;

public class ConditionRequirement<T> extends Requirement<Condition<T>> {
  private final String id;
  private final Class<T> type;

  public ConditionRequirement(String id, Class<T> type) {
    this.id = id;
    this.type = type;
  }

  public ConditionRequirement(Preparation<T> preparation) {
    this.id = preparation.id();
    this.type = preparation.type();
  }

  public String id() {
    return id;
  }

  public Class<T> type() {
    return type;
  }

  public CompletableFuture<? extends T> request() {
    throw new UnsupportedOperationException();
  }

  public T acquire() {
    return request().join();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<Preparation<T>> resolveDependency(Production<?> capability) {
    return capability instanceof Preparation<?>
        && type().isAssignableFrom(((Preparation<?>) capability).type())
            ? Optional.of((Preparation<T>) capability)
            : Optional.empty();
  }

  @Override
  public Stream<Preparation<T>> resolveDependencies(Conductor<?, ?> procedure) {
    return procedure.preparations().map(this::resolveDependency).flatMap(Optional::stream);
  }
}
