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
 * This file is part of uk.co.saiman.fx.
 *
 * uk.co.saiman.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.fx.bindings;

import java.util.function.Function;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableValue;

public abstract class FluentDoubleBinding extends DoubleBinding {
  public static FluentDoubleBinding over(ObservableValue<Number> value) {
    return new FluentDoubleBinding() {
      {
        bind(value);
      }

      @Override
      protected double computeValue() {
        return value.getValue().doubleValue();
      }
    };
  }

  public FluentDoubleBinding map(Function<Double, Double> mapping) {
    return new FluentDoubleMap(this, mapping);
  }

  public <U> FluentObjectBinding<U> mapToObject(Function<Double, U> mapping) {
    return new FluentDoubleMapToObject<>(this, mapping);
  }
}