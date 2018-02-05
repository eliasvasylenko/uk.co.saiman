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
 * This file is part of uk.co.saiman.msapex.chart.
 *
 * uk.co.saiman.msapex.chart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.chart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.chart;

import uk.co.saiman.reflection.token.TypeArgument;
import uk.co.saiman.reflection.token.TypeToken;

/**
 * A typed data annotation on a chart at a specific location.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          The type of the data of the annotation
 */
public interface ChartAnnotation<T> {
  /**
   * @return The data of the annotation
   */
  T getData();

  /**
   * @return The type of the data of the annotation
   */
  TypeToken<T> getDataType();

  default TypeToken<ChartAnnotation<T>> getThisTypeToken() {
    return new TypeToken<ChartAnnotation<T>>() {}
        .withTypeArguments(new TypeArgument<T>(getDataType()) {});
  }

  /**
   * @return The position in the domain of the chart data
   */
  double getX();

  /**
   * @return The position in the codomain of the chart data
   */
  double getY();
}
