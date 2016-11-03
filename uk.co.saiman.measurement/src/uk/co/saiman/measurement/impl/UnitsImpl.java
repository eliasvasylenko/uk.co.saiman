/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.text.NumberFormat;
import java.util.Locale;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.UnitFormat;
import javax.measure.quantity.AmountOfSubstance;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import si.uom.SI;
import tec.uom.se.format.LocalUnitFormat;
import tec.uom.se.format.QuantityFormat;
import uk.co.saiman.measurement.UnitBuilder;
import uk.co.strangeskies.text.properties.LocaleProvider;

@Component
public class UnitsImpl implements uk.co.saiman.measurement.Units {
	@Reference
	LocaleProvider localeProvider;
	private Locale locale;
	private LocalUnitFormat unitFormat;
	private QuantityFormat quantityFormat;

	protected <T extends Quantity<T>> UnitBuilder<T> over(Unit<T> unit) {
		return new UnitBuilderImpl<>(this, unit);
	}

	@Override
	public UnitBuilder<Length> metre() {
		return over(SI.METRE);
	}

	@Override
	public UnitBuilder<Dimensionless> count() {
		return over(SI.ONE);
	}

	@Override
	public UnitBuilder<Time> second() {
		return over(SI.SECOND);
	}

	@Override
	public UnitBuilder<Dimensionless> percent() {
		return over(SI.PERCENT);
	}

	@Override
	public UnitBuilder<Mass> dalton() {
		return over(SI.UNIFIED_ATOMIC_MASS);
	}

	@Override
	public UnitBuilder<AmountOfSubstance> mole() {
		return over(SI.MOLE);
	}

	@Override
	public UnitBuilder<Mass> gram() {
		return over(SI.GRAM);
	}

	@Override
	public String format(Unit<?> unit) {
		return getUnitFormat().format(unit);
	}

	@Override
	public String format(Quantity<?> quantity) {
		return getQuantityFormat().format(quantity);
	}

	@Override
	public String format(Quantity<?> quantity, NumberFormat format) {
		return QuantityFormat.getInstance(format, getUnitFormat()).format(quantity);
	}

	private UnitFormat getUnitFormat() {
		updateFormats();
		return unitFormat;
	}

	private QuantityFormat getQuantityFormat() {
		updateFormats();
		return quantityFormat;
	}

	private void updateFormats() {
		Locale locale = localeProvider.getLocale();
		if (!locale.equals(this.locale)) {
			this.locale = locale;
			unitFormat = LocalUnitFormat.getInstance(locale);

			Unit<?> Da = dalton().get();
			unitFormat.label(Da, "Da");

			quantityFormat = QuantityFormat.getInstance(NumberFormat.getInstance(), unitFormat);
		}
	}
}
