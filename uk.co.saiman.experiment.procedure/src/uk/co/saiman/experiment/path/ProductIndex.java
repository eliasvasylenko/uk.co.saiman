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

import uk.co.saiman.experiment.product.Production;

public class ProductIndex implements Comparable<ProductIndex> {
  private final String productId;

  ProductIndex(String productId) {
    this.productId = productId;
  }

  public static ProductIndex define(String productId) {
    return new ProductIndex(productId);
  }

  public static ProductIndex forProduction(Production<?> production) {
    return new ProductIndex(production.id());
  }

  public String getProductId() {
    return productId;
  }

  public static ProductIndex fromString(String string) {
    return define(string.strip());
  }

  @Override
  public String toString() {
    return productId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    var that = (ProductIndex) obj;

    return Objects.equals(this.productId, that.productId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productId);
  }

  @Override
  public int compareTo(ProductIndex that) {
    return this.productId.compareTo(that.productId);
  }
}
