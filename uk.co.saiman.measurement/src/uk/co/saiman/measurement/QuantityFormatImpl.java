package uk.co.saiman.measurement;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

import javax.measure.Quantity;
import javax.measure.Unit;

import tec.uom.se.AbstractUnit;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

class QuantityFormatImpl implements QuantityFormat {
  private final UnitFormat unitFormat;
  private final NumberFormat numberFormat;

  public QuantityFormatImpl() {
    this(new UnitFormatImpl(), new DecimalFormat());
  }

  public QuantityFormatImpl(UnitFormat unitFormat, NumberFormat numberFormat) {
    this.unitFormat = unitFormat;
    this.numberFormat = numberFormat;
  }

  @Override
  public String format(Quantity<?> quantity) {
    String string = this.numberFormat.format(quantity.getValue());
    if (quantity.getUnit().equals(AbstractUnit.ONE)) {
      return string;
    }
    return string + ' ' + this.unitFormat.format(quantity.getUnit());
  }

  @Override
  public ComparableQuantity<?> parse(CharSequence csq, ParsePosition cursor) {
    final String str = csq.toString();
    final Number number = this.numberFormat.parse(str, cursor);
    if (number == null) {
      throw new IllegalArgumentException("Number cannot be parsed");
    }
    final Unit<?> unit = this.unitFormat.parse(str, cursor);
    return Quantities.getQuantity(number, unit);
  }

  @Override
  public QuantityFormat withNumberFormat(NumberFormat numberFormat) {
    return new QuantityFormatImpl(unitFormat, numberFormat);
  }

  @Override
  public QuantityFormat withUnitFormat(UnitFormat unitFormat) {
    return new QuantityFormatImpl(unitFormat, numberFormat);
  }
}
