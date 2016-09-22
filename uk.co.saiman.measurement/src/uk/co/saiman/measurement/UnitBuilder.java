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
