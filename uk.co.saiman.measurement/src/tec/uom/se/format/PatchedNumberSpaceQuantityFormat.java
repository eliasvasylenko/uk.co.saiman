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
package tec.uom.se.format;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.ParserException;
import javax.measure.format.UnitFormat;

import tec.uom.se.AbstractUnit;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

public class PatchedNumberSpaceQuantityFormat extends QuantityFormat {
	private final NumberFormat numberFormat;
	private final UnitFormat unitFormat;
	private static final long serialVersionUID = 1L;

	public PatchedNumberSpaceQuantityFormat(final NumberFormat numberFormat, final UnitFormat unitFormat) {
		this.numberFormat = numberFormat;
		this.unitFormat = unitFormat;
	}

	static int getFractionDigitsCount(double d) {
		if (d >= 1.0) {
			d -= (long) d;
		}
		if (d == 0.0) {
			return 0;
		}
		int count;
		for (d *= 10.0, count = 1; d - (long) d != 0.0; d *= 10.0, ++count) {}
		return count;
	}

	@Override
	public Appendable format(Quantity<?> quantity, Appendable dest) throws IOException {
		int fract = 0;
		if (quantity != null && quantity.getValue() != null) {
			fract = getFractionDigitsCount(quantity.getValue().doubleValue());
		}
		if (fract > 1) {
			this.numberFormat.setMaximumFractionDigits(fract + 1);
		}
		dest.append(this.numberFormat.format(quantity.getValue()));
		if (quantity.getUnit().equals(AbstractUnit.ONE)) {
			return dest;
		}
		dest.append(' ');
		return this.unitFormat.format(quantity.getUnit(), dest);
	}

	@Override
	public ComparableQuantity<?> parse(CharSequence csq, ParsePosition cursor)
			throws IllegalArgumentException, ParserException {
		final String str = csq.toString();
		final Number number = this.numberFormat.parse(str, cursor);
		if (number == null) {
			throw new IllegalArgumentException("Number cannot be parsed");
		}
		final Unit<?> unit = this.unitFormat.parse(str.substring(cursor.getIndex()));
		return Quantities.getQuantity(number, unit);
	}

	@Override
	public ComparableQuantity<?> parse(CharSequence csq, int index) throws IllegalArgumentException, ParserException {
		return this.parse(csq, new ParsePosition(index));
	}

	@Override
	public ComparableQuantity<?> parse(CharSequence csq) throws IllegalArgumentException, ParserException {
		return this.parse(csq, 0);
	}
}
