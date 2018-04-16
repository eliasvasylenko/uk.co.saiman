package uk.co.saiman.measurement;

import java.text.ParsePosition;

import javax.measure.Unit;

public interface UnitFormat {
  Unit<?> parse(CharSequence characters, ParsePosition cursor);

  default Unit<?> parse(String unit) {
    unit = unit.trim();
    ParsePosition position = new ParsePosition(0);
    Unit<?> parsed = parse(unit, position);
    if (position.getIndex() != unit.length())
      throw new IllegalArgumentException("Could not parse full string");
    return parsed;
  }

  String format(Unit<?> unit);

  // TODO UnitFormat withLocale(Locale locale);
}
