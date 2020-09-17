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

/**
 * An immutable piece of data which can easily be transformed according to
 * {@link Accessor type-safe accessors}.
 * 
 * @author Elias N Vasylenko
 */
public interface State {
  StateKind getKind();

  default State as(StateKind kind) {
    if (getKind() != kind) {
      throw new UnexpectedStateKindException(kind, getKind());
    }
    return this;
  }

  default StateProperty asProperty() {
    return (StateProperty) as(PROPERTY);
  }

  default StateMap asMap() {
    return (StateMap) as(MAP);
  }

  default StateList asList() {
    return (StateList) as(LIST);
  }
}
