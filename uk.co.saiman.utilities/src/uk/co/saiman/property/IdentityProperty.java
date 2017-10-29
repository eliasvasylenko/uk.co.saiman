/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.property;

/**
 * A basic implementation of {@link Property} which simple stores it's value as
 * a member variable, which can be updated and retrieved through get and set.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          The type of the property.
 */
/* @I */
public class IdentityProperty<T> implements Property<T> {
  private/* @I */T value;

  /**
   * Create an IndentityProperty with null as the initial value.
   */
  public IdentityProperty() {}

  /**
   * Create an identity with the given initial value.
   * 
   * @param value
   *          The initial value for this property.
   */
  public IdentityProperty(T value) {
    this.value = value;
  }

  @Override
  public T set(/* @Mutable IdentityProperty<T> this, */T to) {
    value = to;
    return value;
  }

  @Override
  public/* @I */T get() {
    return value;
  }
}
