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
 * This file is part of uk.co.saiman.experiment.production.
 *
 * uk.co.saiman.experiment.production is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.production is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.requirement;

import java.util.Objects;

import uk.co.saiman.experiment.production.Product;
import uk.co.saiman.experiment.production.Production;

public abstract class ProductRequirement<T extends Product> extends Requirement<T> {
  private final Production<T> production;

  ProductRequirement(Production<T> production) {
    this.production = production;
  }

  public Production<T> production() {
    return production;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ProductRequirement<?>)) {
      return false;
    }

    var that = (ProductRequirement<?>) obj;

    return Objects.equals(this.production, that.production);
  }

  @Override
  public int hashCode() {
    return production.hashCode();
  }
}
