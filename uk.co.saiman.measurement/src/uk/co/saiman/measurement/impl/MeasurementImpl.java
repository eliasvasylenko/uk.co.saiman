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
public class MeasurementImpl implements uk.co.saiman.measurement.Units {
	public class UnitBuilderImpl<T extends Quantity<T>> implements UnitBuilder<T> {
		private final Unit<T> unit;

		public UnitBuilderImpl(Unit<T> unit) {
			this.unit = unit;
		}

		@Override
		public UnitBuilder<?> multiply(Function<uk.co.saiman.measurement.Units, UnitBuilder<?>> unit) {
			return new UnitBuilderImpl<>(this.unit.multiply(unit.apply(MeasurementImpl.this).get()));
		}

		@Override
		public UnitBuilder<?> divide(Function<uk.co.saiman.measurement.Units, UnitBuilder<?>> unit) {
			return new UnitBuilderImpl<>(this.unit.divide(unit.apply(MeasurementImpl.this).get()));
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
