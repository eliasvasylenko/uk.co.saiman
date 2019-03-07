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

public class ProductPath implements Comparable<ProductPath> {
  private final ExperimentPath experimentPath;
  private final String productId;

  ProductPath(ExperimentPath experimentPath, String productId) {
    this.experimentPath = experimentPath;
    this.productId = productId;
  }

  public static ProductPath define(ExperimentPath experimentPath, String productId) {
    return new ProductPath(experimentPath, productId);
  }

  public ExperimentPath getExperimentPath() {
    return experimentPath;
  }

  public String getProductId() {
    return productId;
  }

  public static ProductPath fromString(String string) {
    string = string.strip();

    int lastSlash = string.lastIndexOf('/');

    return define(
        ExperimentPath.fromString(string.substring(0, lastSlash)),
        string.substring(lastSlash + 1));
  }

  @Override
  public String toString() {
    return experimentPath + productId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    var that = (ProductPath) obj;

    return Objects.equals(this.experimentPath, that.experimentPath)
        && Objects.equals(this.productId, that.productId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(experimentPath, productId);
  }

  @Override
  public int compareTo(ProductPath that) {
    int comparePath = this.experimentPath.compareTo(that.experimentPath);
    return comparePath != 0 ? comparePath : this.productId.compareTo(that.productId);
  }
}
