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
package uk.co.saiman.measurement.impl;

import java.util.function.Function;

import javax.measure.Quantity;
import javax.measure.Unit;

import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.MetricPrefix;
import uk.co.saiman.measurement.UnitBuilder;

public class UnitBuilderImpl<T extends Quantity<T>> implements UnitBuilder<T> {
	private final UnitsImpl unitsImpl;
	private final Unit<T> unit;

	public UnitBuilderImpl(UnitsImpl unitsImpl, Unit<T> unit) {
		this.unitsImpl = unitsImpl;
		this.unit = unit;
	}

	@Override
	public UnitBuilder<?> multiply(Function<uk.co.saiman.measurement.Units, UnitBuilder<?>> unit) {
		return new UnitBuilderImpl<>(unitsImpl, this.unit.multiply(unit.apply(unitsImpl).get()));
	}

	@Override
	public UnitBuilder<?> divide(Function<uk.co.saiman.measurement.Units, UnitBuilder<?>> unit) {
		return new UnitBuilderImpl<>(unitsImpl, this.unit.divide(unit.apply(unitsImpl).get()));
	}

	@Override
	public UnitBuilder<T> yotta() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.YOTTA(unit));
	}

	@Override
	public UnitBuilder<T> zetta() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.ZETTA(unit));
	}

	@Override
	public UnitBuilder<T> exa() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.EXA(unit));
	}

	@Override
	public UnitBuilder<T> peta() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.PETA(unit));
	}

	@Override
	public UnitBuilder<T> tera() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.TERA(unit));
	}

	@Override
	public UnitBuilder<T> giga() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.GIGA(unit));
	}

	@Override
	public UnitBuilder<T> mega() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.MEGA(unit));
	}

	@Override
	public UnitBuilder<T> kilo() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.KILO(unit));
	}

	@Override
	public UnitBuilder<T> hecto() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.HECTO(unit));
	}

	@Override
	public UnitBuilder<T> deka() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.DEKA(unit));
	}

	@Override
	public UnitBuilder<T> deci() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.DECI(unit));
	}

	@Override
	public UnitBuilder<T> centi() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.CENTI(unit));
	}

	@Override
	public UnitBuilder<T> milli() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.MILLI(unit));
	}

	@Override
	public UnitBuilder<T> micro() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.MICRO(unit));
	}

	@Override
	public UnitBuilder<T> nano() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.NANO(unit));
	}

	@Override
	public UnitBuilder<T> pico() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.PICO(unit));
	}

	@Override
	public UnitBuilder<T> femto() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.FEMTO(unit));
	}

	@Override
	public UnitBuilder<T> atto() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.ATTO(unit));
	}

	@Override
	public UnitBuilder<T> zepto() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.ZEPTO(unit));
	}

	@Override
	public UnitBuilder<T> yocto() {
		return new UnitBuilderImpl<>(unitsImpl, MetricPrefix.YOCTO(unit));
	}

	@Override
	public Unit<T> get() {
		return unit;
	}

	@Override
	public Quantity<T> getQuantity(Number amount) {
		return Quantities.getQuantity(amount, unit);
	}
}
