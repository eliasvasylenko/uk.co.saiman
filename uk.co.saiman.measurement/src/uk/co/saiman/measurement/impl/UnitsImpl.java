/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Component;

import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;
import uk.co.saiman.measurement.UnitBuilder;

@Component
public class UnitsImpl implements uk.co.saiman.measurement.Units {
	public class UnitBuilderImpl<T extends Quantity<T>> implements UnitBuilder<T> {
		private final Unit<T> unit;

		public UnitBuilderImpl(Unit<T> unit) {
			this.unit = unit;
		}

		@Override
		public UnitBuilder<?> multiply(Function<uk.co.saiman.measurement.Units, UnitBuilder<?>> unit) {
			return new UnitBuilderImpl<>(this.unit.multiply(unit.apply(UnitsImpl.this).get()));
		}

		@Override
		public UnitBuilder<?> divide(Function<uk.co.saiman.measurement.Units, UnitBuilder<?>> unit) {
			return new UnitBuilderImpl<>(this.unit.divide(unit.apply(UnitsImpl.this).get()));
		}

		@Override
		public UnitBuilder<T> milli() {
			return new UnitBuilderImpl<>(MetricPrefix.MILLI(unit));
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

	@Override
	public UnitBuilder<Length> metre() {
		return over(Units.METRE);
	}

	protected <T extends Quantity<T>> UnitBuilder<T> over(Unit<T> unit) {
		return new UnitBuilderImpl<>(unit);
	}
}
