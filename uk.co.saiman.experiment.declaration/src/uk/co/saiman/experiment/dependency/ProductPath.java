/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.declaration.
 *
 * uk.co.saiman.experiment.declaration is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.declaration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.dependency;

import static uk.co.saiman.collection.EquivalenceComparator.identityComparator;

import java.util.Objects;
import java.util.Optional;

import uk.co.saiman.collection.EquivalenceComparator;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;

public abstract class ProductPath<T extends ExperimentPath<T>, U extends Product<?>>
    implements Comparable<ProductPath<?, ?>> {
  private static final EquivalenceComparator<Class<?>> IDENTITY_COMPARATOR = identityComparator();

  private final ExperimentPath<T> experimentPath;
  private final Class<?> production;

  ProductPath(ExperimentPath<T> experimentPath, Class<?> production) {
    this.experimentPath = experimentPath;
    this.production = production;
  }

  public static <T extends ExperimentPath<T>, U> ResultPath<T, U> toResult(
      ExperimentPath<T> experimentPath,
      Class<U> type) {
    return new ResultPath<>(experimentPath, type);
  }

  public static <T extends ExperimentPath<T>, U> ConditionPath<T, U> toCondition(
      ExperimentPath<T> experimentPath,
      Class<U> type) {
    return new ConditionPath<>(experimentPath, type);
  }

  public ExperimentPath<T> getExperimentPath() {
    return experimentPath;
  }

  public Class<?> getProduction() {
    return production;
  }

  abstract <V extends ExperimentPath<V>> ProductPath<V, U> moveTo(ExperimentPath<V> experimentPath);

  public abstract Optional<? extends ProductPath<Absolute, U>> resolveAgainst(
      ExperimentPath<Absolute> path);

  public abstract ProductPath<Absolute, U> toAbsolute();

  @Override
  public String toString() {
    return experimentPath.toString() + production.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    var that = (ProductPath<?, ?>) obj;

    return Objects.equals(this.experimentPath, that.experimentPath)
        && Objects.equals(this.production, that.production);
  }

  @Override
  public int hashCode() {
    return Objects.hash(experimentPath, production);
  }

  @Override
  public int compareTo(ProductPath<?, ?> that) {
    int comparePath = this.experimentPath.compareTo(that.experimentPath);
    int compareProductionId = this.production
        .getClass()
        .getName()
        .compareTo(that.production.getClass().getName());
    return comparePath != 0
        ? comparePath
        : compareProductionId != 0
            ? compareProductionId
            : IDENTITY_COMPARATOR.compare(this.production, that.production);
  }
}
