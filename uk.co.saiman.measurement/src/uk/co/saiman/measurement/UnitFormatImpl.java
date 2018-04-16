package uk.co.saiman.measurement;

import static uk.co.saiman.measurement.Units.dalton;

import java.text.ParsePosition;

import javax.measure.Unit;

import tec.uom.se.format.SimpleUnitFormat;

class UnitFormatImpl implements UnitFormat {
  private final SimpleUnitFormat unitFormat;

  public UnitFormatImpl() {
    unitFormat = SimpleUnitFormat.getInstance();
    unitFormat.label(dalton().getUnit(), "Da");
  }

  @Override
  public Unit<?> parse(CharSequence characters, ParsePosition cursor) {
    return unitFormat.parseObject(characters.toString(), cursor);
  }

  @Override
  public String format(Unit<?> unit) {
    return unitFormat.format(unit);
  }
}
