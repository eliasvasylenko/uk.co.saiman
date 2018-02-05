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
 * This file is part of uk.co.saiman.measurement.
 *
 * uk.co.saiman.measurement is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.measurement is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.measurement;

import java.util.function.Function;

import javax.measure.Quantity;
import javax.measure.Unit;

public interface UnitBuilder<T extends Quantity<T>> {
  /*
   * metric prefixes
   */

  UnitBuilder<T> yotta();

  UnitBuilder<T> zetta();

  UnitBuilder<T> exa();

  UnitBuilder<T> peta();

  UnitBuilder<T> tera();

  UnitBuilder<T> giga();

  UnitBuilder<T> mega();

  UnitBuilder<T> kilo();

  UnitBuilder<T> hecto();

  UnitBuilder<T> deka();

  UnitBuilder<T> deci();

  UnitBuilder<T> centi();

  UnitBuilder<T> milli();

  UnitBuilder<T> micro();

  UnitBuilder<T> nano();

  UnitBuilder<T> pico();

  UnitBuilder<T> femto();

  UnitBuilder<T> atto();

  UnitBuilder<T> zepto();

  UnitBuilder<T> yocto();

  /*
   * products and powers
   */

  UnitBuilder<?> multiply(Function<Units, UnitBuilder<?>> unit);

  UnitBuilder<?> divide(Function<Units, UnitBuilder<?>> unit);

  /*
   * build
   */

  Unit<T> get();

  Quantity<T> getQuantity(Number amount);
}
