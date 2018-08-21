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
 * This file is part of uk.co.saiman.utilities.
 *
 * uk.co.saiman.utilities is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.utilities is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.utility;

/**
 * An object which can be copied.
 * 
 * @author Elias N Vasylenko
 *
 * @param <S>
 *          self bounding on the type of the object, so that copies can be
 *          correctly typed
 */
public interface Copyable<S extends Copyable<S>> {
  /**
   * @return a copy of the receiving instance
   */
  S copy();

  /**
   * @param context
   *          an object graph {@link Isomorphism}
   * @return a deep copy of the receiving instance, consistent with the given
   *         {@link Isomorphism}
   */
  default S deepCopy(Isomorphism context) {
    return context.byIdentity().getCopy(this);
  }

  default S deepCopy() {
    return deepCopy(new Isomorphism());
  }
}
