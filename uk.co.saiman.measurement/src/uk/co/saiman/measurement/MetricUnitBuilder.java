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

import javax.measure.Quantity;

public interface MetricUnitBuilder<T extends Quantity<T>> extends UnitBuilder<T> {
  /*
   * metric prefixes
   */

  MetricUnitBuilder<T> yotta();

  MetricUnitBuilder<T> zetta();

  MetricUnitBuilder<T> exa();

  MetricUnitBuilder<T> peta();

  MetricUnitBuilder<T> tera();

  MetricUnitBuilder<T> giga();

  MetricUnitBuilder<T> mega();

  MetricUnitBuilder<T> kilo();

  MetricUnitBuilder<T> hecto();

  MetricUnitBuilder<T> deka();

  MetricUnitBuilder<T> none();

  MetricUnitBuilder<T> deci();

  MetricUnitBuilder<T> centi();

  MetricUnitBuilder<T> milli();

  MetricUnitBuilder<T> micro();

  MetricUnitBuilder<T> nano();

  MetricUnitBuilder<T> pico();

  MetricUnitBuilder<T> femto();

  MetricUnitBuilder<T> atto();

  MetricUnitBuilder<T> zepto();

  MetricUnitBuilder<T> yocto();
}
