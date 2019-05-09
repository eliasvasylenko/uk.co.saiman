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
 * This file is part of uk.co.saiman.experiment.graph.
 *
 * uk.co.saiman.experiment.graph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.graph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.production;

import static uk.co.saiman.collection.EquivalenceComparator.identityComparator;

import java.util.Objects;
import java.util.Optional;

import uk.co.saiman.collection.EquivalenceComparator;
import uk.co.saiman.experiment.graph.ExperimentPath;
import uk.co.saiman.experiment.graph.ExperimentPath.Absolute;

public class ProductPath<T extends ExperimentPath<T>, U extends Product>
    implements Comparable<ProductPath<?, ?>> {
  private final EquivalenceComparator<Production<?>> productionComparator;

  private final ExperimentPath<T> experimentPath;
  private final Production<U> production;

  ProductPath(ExperimentPath<T> experimentPath, Production<U> production) {
    this.experimentPath = experimentPath;
    this.production = production;
    this.productionComparator = identityComparator();
  }

  public static <T extends ExperimentPath<T>, U extends Product> ProductPath<T, U> define(
      ExperimentPath<T> experimentPath,
      Production<U> production) {
    return new ProductPath<>(experimentPath, production);
  }

  public ExperimentPath<T> getExperimentPath() {
    return experimentPath;
  }

  public Production<U> getProduction() {
    return production;
  }

  public Optional<ProductPath<Absolute, U>> resolveAgainst(ExperimentPath<Absolute> path) {
    return experimentPath
        .resolveAgainst(path)
        .map(experimentPath -> define(experimentPath, production));
  }

  public ProductPath<Absolute, U> toAbsolute() {
    return define(experimentPath.toAbsolute(), production);
  }

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
    int compareProductionId = this.production.id().compareTo(that.production.id());
    return comparePath != 0
        ? comparePath
        : compareProductionId != 0
            ? compareProductionId
            : productionComparator.compare(this.production, that.production);
  }
}
