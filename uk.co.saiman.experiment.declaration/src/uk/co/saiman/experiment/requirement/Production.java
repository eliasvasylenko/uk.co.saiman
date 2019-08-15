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
package uk.co.saiman.experiment.requirement;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.experiment.dependency.Product;

/**
 * A production is simply a representation of an API point. In particular, it
 * represents an artifact which can be produced as a result of conducting a
 * procedure, and the Java type which the product may be materialized as.
 * <p>
 * Production instances are intended to be static, and do not prescribe the
 * method of collecting or storing the product data. The data should be stored
 * according to a {@link DataFormat format} which is compatible with the type of
 * the product.
 */
public abstract class Production<T, U extends Product<T>> extends Requirement<T, U> {
  Production(Class<?> type) {
    super(type);
  }
}
