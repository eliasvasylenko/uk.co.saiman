package uk.co.saiman.msapex.chart;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.util.StringConverter;

public interface TickUnit<T extends Quantity<T>> {
  Unit<T> unit();

  StringConverter<Number> format();

  double majorTick();

  int minorTickCount();

  TickUnit<T> unitAbove();
}