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

import java.util.Collection;
import java.util.Set;

import javafx.beans.value.ObservableValue;

public class FluentObjectDependent<T> extends FluentObjectBinding<T> {
  private final ObservableValue<T> value;
  private final Collection<ObservableValue<?>> dependencies;

  public FluentObjectDependent(ObservableValue<T> value, ObservableValue<?>... dependencies) {
    this.value = value;
    this.dependencies = Set.of(dependencies);
    bind(value);
    bind(dependencies);
  }

  @Override
  protected T computeValue() {
    if (dependencies.stream().anyMatch(d -> d.getValue() == null)) {
      return null;
    }
    return value.getValue();
  }
}
