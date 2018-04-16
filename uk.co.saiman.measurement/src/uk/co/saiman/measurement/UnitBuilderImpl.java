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

import javax.measure.Quantity;
import javax.measure.Unit;

import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.MetricPrefix;

class UnitBuilderImpl<T extends Quantity<T>> implements MetricUnitBuilder<T> {
  private final Unit<T> unit;
  private final MetricPrefix prefix;

  public UnitBuilderImpl(Unit<T> unit, MetricPrefix prefix) {
    this.unit = unit;
    this.prefix = prefix;
  }

  @Override
  public UnitBuilder<?> multiply(UnitBuilder<?> unit) {
    return new UnitBuilderImpl<>(this.unit.multiply(unit.getUnit()), prefix);
  }

  @Override
  public UnitBuilderImpl<?> divide(UnitBuilder<?> unit) {
    return new UnitBuilderImpl<>(this.unit.divide(unit.getUnit()), prefix);
  }

  @Override
  public UnitBuilderImpl<T> yotta() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.YOTTA);
  }

  @Override
  public UnitBuilderImpl<T> zetta() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.ZETTA);
  }

  @Override
  public UnitBuilderImpl<T> exa() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.EXA);
  }

  @Override
  public UnitBuilderImpl<T> peta() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.PETA);
  }

  @Override
  public UnitBuilderImpl<T> tera() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.TERA);
  }

  @Override
  public UnitBuilderImpl<T> giga() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.GIGA);
  }

  @Override
  public UnitBuilderImpl<T> mega() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.MEGA);
  }

  @Override
  public UnitBuilderImpl<T> kilo() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.KILO);
  }

  @Override
  public UnitBuilderImpl<T> hecto() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.HECTO);
  }

  @Override
  public UnitBuilderImpl<T> deka() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.DEKA);
  }

  @Override
  public MetricUnitBuilder<T> none() {
    return new UnitBuilderImpl<>(unit, null);
  }

  @Override
  public UnitBuilderImpl<T> deci() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.DECI);
  }

  @Override
  public UnitBuilderImpl<T> centi() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.CENTI);
  }

  @Override
  public UnitBuilderImpl<T> milli() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.MILLI);
  }

  @Override
  public UnitBuilderImpl<T> micro() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.MICRO);
  }

  @Override
  public UnitBuilderImpl<T> nano() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.NANO);
  }

  @Override
  public UnitBuilderImpl<T> pico() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.PICO);
  }

  @Override
  public UnitBuilderImpl<T> femto() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.FEMTO);
  }

  @Override
  public UnitBuilderImpl<T> atto() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.ATTO);
  }

  @Override
  public UnitBuilderImpl<T> zepto() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.ZEPTO);
  }

  @Override
  public UnitBuilderImpl<T> yocto() {
    return new UnitBuilderImpl<>(unit, MetricPrefix.YOCTO);
  }

  @Override
  public Unit<T> getUnit() {
    return prefix == null ? unit : unit.transform(prefix.getConverter());
  }

  @Override
  public Quantity<T> getQuantity(Number amount) {
    return Quantities.getQuantity(amount, unit);
  }
}
