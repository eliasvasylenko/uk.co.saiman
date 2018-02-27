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
package uk.co.saiman.measurement.impl;

import java.lang.reflect.Method;
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
import tec.uom.se.AbstractUnit;
import tec.uom.se.format.LocalUnitFormat;
import tec.uom.se.format.PatchedNumberSpaceQuantityFormat;
import tec.uom.se.format.QuantityFormat;
import tec.uom.se.format.SimpleUnitFormat;
import tec.uom.se.format.SymbolMap;
import uk.co.saiman.measurement.UnitBuilder;
import uk.co.saiman.text.properties.LocaleProvider;

@Component
public class UnitsImpl implements uk.co.saiman.measurement.Units {
  @Reference
  LocaleProvider localeProvider;
  private Locale locale;
  private UnitFormat unitFormat;
  private QuantityFormat quantityFormat;

  private static final Unit<Dimensionless> COUNT = AbstractUnit.ONE;

  public <T extends Quantity<T>> UnitBuilder<T> with(Unit<T> unit) {
    return new UnitBuilderImpl<>(this, unit);
  }

  @Override
  public UnitBuilder<Length> metre() {
    return with(SI.METRE);
  }

  @Override
  public UnitBuilder<Dimensionless> count() {
    return with(COUNT);
  }

  @Override
  public UnitBuilder<Time> second() {
    return with(SI.SECOND);
  }

  @Override
  public UnitBuilder<Dimensionless> percent() {
    return with(SI.PERCENT);
  }

  @Override
  public UnitBuilder<Mass> dalton() {
    return with(SI.UNIFIED_ATOMIC_MASS);
  }

  @Override
  public UnitBuilder<AmountOfSubstance> mole() {
    return with(SI.MOLE);
  }

  @Override
  public UnitBuilder<Mass> gram() {
    return with(SI.GRAM);
  }

  @Override
  public Unit<?> parseUnit(String unit) {
    return getUnitFormat().parse(unit);
  }

  @Override
  public String formatUnit(Unit<?> unit) {
    return getUnitFormat().format(unit);
  }

  @Override
  public Quantity<?> parseQuantity(String unit) {
    return getQuantityFormat().parse(unit);
  }

  @Override
  public String formatQuantity(Quantity<?> quantity) {
    return getQuantityFormat().format(quantity);
  }

  @Override
  public String formatQuantity(Quantity<?> quantity, NumberFormat format) {
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
    if (this.locale == null) {
      this.locale = locale;

      unitFormat = addLabels(SimpleUnitFormat.getInstance());
      quantityFormat = new PatchedNumberSpaceQuantityFormat(NumberFormat.getInstance(), unitFormat);
    }
  }

  private SimpleUnitFormat addLabels(SimpleUnitFormat instance) {
    Unit<?> Da = dalton().get();
    instance.label(Da, "Da");

    return instance;
  }

  private void updateFormatsLocal() {
    Locale locale = localeProvider.getLocale();
    if (!locale.equals(this.locale)) {
      this.locale = locale;

      unitFormat = addLabels(LocalUnitFormat.getInstance(locale));
      quantityFormat = QuantityFormat.getInstance(NumberFormat.getInstance(locale), unitFormat);
    }
  }

  private LocalUnitFormat addLabels(LocalUnitFormat instance) {
    try {
      Method getSymbolsMethod = instance.getClass().getDeclaredMethod("getSymbols");
      getSymbolsMethod.setAccessible(true);
      SymbolMap symbolMap = (SymbolMap) getSymbolsMethod.invoke(instance);

      Unit<?> Da = dalton().get();
      symbolMap.label(Da, "Da");

      return instance;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
