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

import static uk.co.saiman.reflection.token.TypeToken.forType;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import uk.co.saiman.reflection.token.TypeParameter;
import uk.co.saiman.reflection.token.TypeToken;

public abstract class ConditionRequirement<T> extends Requirement<Condition<T>> {
  public static <T> ConditionRequirement<T> conditionRequirement(Preparation<T> condition) {
    throw new UnsupportedOperationException();
  }

  private final String id;

  protected ConditionRequirement(String id) {
    this.id = id;
  }

  protected ConditionRequirement(Preparation<T> preparation) {
    this.id = preparation.id();
  }

  public String id() {
    return id;
  }

  public CompletableFuture<? extends T> request() {
    throw new UnsupportedOperationException();
  }

  public T acquire() {
    return request().join();
  }

  public TypeToken<T> getConditionType() {
    return forType(getClass())
        .resolveSupertype(Procedure.class)
        .resolveTypeArgument(new TypeParameter<T>() {})
        .getTypeToken();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<Preparation<T>> resolveCapability(Capability<?> capability) {
    return capability instanceof Preparation<?>
        && ((Preparation<?>) capability).getConditionType().isAssignableTo(getConditionType())
            ? Optional.of((Preparation<T>) capability)
            : Optional.empty();
  }

  @Override
  public Stream<Preparation<T>> resolveCapabilities(Procedure<?> procedure) {
    return procedure.preparations().map(this::resolveCapability).flatMap(Optional::stream);
  }
}
