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

import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;

public class Dependency<T extends Product> {
  private final ExperimentPath experimentPath;
  private final Production<T> production;

  Dependency(ExperimentPath experimentPath, Production<T> production) {
    this.experimentPath = experimentPath;
    this.production = production;
  }

  public static <T extends Product> Dependency<T> define(
      ExperimentPath experimentPath,
      Production<T> production) {
    return new Dependency<>(experimentPath, production);
  }

  public ExperimentPath getExperimentPath() {
    return experimentPath;
  }

  public Production<T> getProduction() {
    return production;
  }

  public ProductPath getProductPath() {
    return experimentPath.resolve(production);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    var that = (Dependency<?>) obj;

    return Objects.equals(this.experimentPath, that.experimentPath)
        && Objects.equals(this.production, that.production);
  }

  @Override
  public int hashCode() {
    return Objects.hash(experimentPath, production);
  }
}
