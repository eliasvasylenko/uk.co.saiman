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
package uk.co.saiman.experiment.path;

import java.util.Objects;
import java.util.Optional;

import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;

public class Dependency<T extends Product, U extends ExperimentPath<U>> {
  private final ExperimentPath<U> experimentPath;
  private final Production<T> production;

  Dependency(ExperimentPath<U> experimentPath, Production<T> production) {
    this.experimentPath = experimentPath;
    this.production = production;
  }

  public ExperimentPath<U> getExperimentPath() {
    return experimentPath;
  }

  public Production<T> getProduction() {
    return production;
  }

  public ProductPath<U> getProductPath() {
    return experimentPath.resolve(ProductIndex.forProduction(production));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    var that = (Dependency<?, ?>) obj;

    return Objects.equals(this.experimentPath, that.experimentPath)
        && Objects.equals(this.production, that.production);
  }

  @Override
  public int hashCode() {
    return Objects.hash(experimentPath, production);
  }

  public Optional<Dependency<T, Absolute>> resolveAgainst(ExperimentPath<Absolute> path) {
    return experimentPath.resolveAgainst(path).map(p -> p.resolve(production));
  }

  public Dependency<T, Absolute> toAbsolute() {
    return resolveAgainst(ExperimentPath.defineAbsolute()).get();
  }
}
