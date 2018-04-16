package uk.co.saiman.measurement;

import java.text.NumberFormat;
import java.text.ParsePosition;

import javax.measure.Quantity;

public interface QuantityFormat {
  Quantity<?> parse(CharSequence characters, ParsePosition cursor);

  default Quantity<?> parse(String quantity) {
    quantity = quantity.trim();
    ParsePosition position = new ParsePosition(0);
    Quantity<?> parsed = parse(quantity, position);
    if (position.getIndex() != quantity.length())
      throw new IllegalArgumentException(
          "Could not parse full string "
              + quantity
              + " "
              + position.getIndex()
              + " "
              + quantity.length());
    return parsed;
  }

  String format(Quantity<?> quantity);

  QuantityFormat withNumberFormat(NumberFormat format);

  QuantityFormat withUnitFormat(UnitFormat format);

  // TODO ? QuantityFormat withLocale(Locale locale);
}
