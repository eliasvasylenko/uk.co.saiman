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
 * This file is part of uk.co.saiman.state.
 *
 * uk.co.saiman.state is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.state is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.state;

import static uk.co.saiman.state.StateKind.LIST;
import static uk.co.saiman.state.StateKind.MAP;
import static uk.co.saiman.state.StateKind.PROPERTY;

import java.util.Optional;

/**
 * An immutable piece of data which can easily be transformed according to
 * {@link Accessor type-safe accessors}.
 * 
 * @author Elias N Vasylenko
 */
public interface State {
  StateKind getKind();

  @SuppressWarnings("unchecked")
  default <T, U extends State> Optional<T> tryGet(Accessor<T, U> accessor) {
    return getKind() == accessor.getKind()
        ? Optional.of(accessor.read((U) this))
        : Optional.empty();
  }

  /*
   * TODO with amber generic enums, refactor to only need #as method.
   */

  default State as(StateKind kind) {
    if (getKind() != kind) {
      throw new UnexpectedStateKindException();
    }
    return this;
  }

  default StateProperty asProperty() {
    if (getKind() != PROPERTY) {
      throw new UnexpectedStateKindException();
    }
    return (StateProperty) this;
  }

  default StateMap asMap() {
    if (getKind() != MAP) {
      throw new UnexpectedStateKindException();
    }
    return (StateMap) this;
  }

  default StateList asList() {
    if (getKind() != LIST) {
      throw new UnexpectedStateKindException();
    }
    return (StateList) this;
  }
}
