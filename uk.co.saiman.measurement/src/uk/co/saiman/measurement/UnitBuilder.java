package uk.co.saiman.measurement;

import java.util.function.Function;

import javax.measure.Quantity;
import javax.measure.Unit;

public interface UnitBuilder<T extends Quantity<T>> {
	UnitBuilder<T> milli();

	UnitBuilder<?> multiply(Function<Units, UnitBuilder<?>> unit);

	UnitBuilder<?> divide(Function<Units, UnitBuilder<?>> unit);

	Unit<T> get();

	Quantity<T> getQuantity(Number amount);
}
