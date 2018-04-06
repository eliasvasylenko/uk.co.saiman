package uk.co.saiman.measurement.fx;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import javafx.util.StringConverter;
import uk.co.saiman.measurement.Units;

public interface QuantityFormatter {
  StringConverter<Number> quantityFormatter(Unit<?> unit);

  StringConverter<Number> quantityFormatter(Unit<?> unit, NumberFormat numberFormat);

  StringConverter<Unit<?>> unitFormatter();

  static QuantityFormatter basicQuantityFormatter() {
    return new QuantityFormatter() {
      @Override
      public StringConverter<Unit<?>> unitFormatter() {
        return new StringConverter<Unit<?>>() {
          @Override
          public String toString(Unit<?> object) {
            return object.toString();
          }

          @Override
          public Unit<?> fromString(String string) {
            throw new UnsupportedOperationException();
          }
        };
      }

      @Override
      public StringConverter<Number> quantityFormatter(Unit<?> unit, NumberFormat numberFormat) {
        return new StringConverter<Number>() {
          @Override
          public String toString(Number object) {
            return numberFormat.format(object) + " " + unit.toString();
          }

          @Override
          public Number fromString(String string) {
            String[] split = string.split(" ");
            if (!split[1].equals(unit.toString())) {
              throw new IllegalArgumentException(string);
            }
            try {
              return numberFormat.parse(split[0]);
            } catch (ParseException e) {
              throw new IllegalArgumentException(string, e);
            }
          }
        };
      }

      @Override
      public StringConverter<Number> quantityFormatter(Unit<?> unit) {
        return quantityFormatter(unit, new DecimalFormat());
      }
    };
  }

  static QuantityFormatter quantityFormatter(Units units) {
    return new QuantityFormatter() {
      @Override
      public StringConverter<Number> quantityFormatter(Unit<?> unit) {
        return new StringConverter<Number>() {
          @Override
          public String toString(Number object) {
            return units.formatQuantity(units.getQuantity(unit, object));
          }

          @Override
          public Number fromString(String string) {
            Quantity<?> quantity = units.parseQuantity(string);
            UnitConverter converter;
            try {
              converter = quantity.getUnit().getConverterToAny(unit);
            } catch (UnconvertibleException | IncommensurableException e) {
              throw new IllegalArgumentException(e);
            }
            return converter.convert(quantity.getValue());
          }
        };
      }

      @Override
      public StringConverter<Number> quantityFormatter(Unit<?> unit, NumberFormat numberFormat) {
        return new StringConverter<Number>() {
          @Override
          public String toString(Number object) {
            return units.formatQuantity(units.getQuantity(unit, object), numberFormat);
          }

          @Override
          public Number fromString(String string) {
            Quantity<?> quantity = units.parseQuantity(string, numberFormat);
            UnitConverter converter;
            try {
              converter = quantity.getUnit().getConverterToAny(unit);
            } catch (UnconvertibleException | IncommensurableException e) {
              throw new IllegalArgumentException(e);
            }
            return converter.convert(quantity.getValue());
          }
        };
      }

      @Override
      public StringConverter<Unit<?>> unitFormatter() {
        return new StringConverter<Unit<?>>() {
          @Override
          public String toString(Unit<?> object) {
            return units.formatUnit(object);
          }

          @Override
          public Unit<?> fromString(String string) {
            return units.parseUnit(string);
          }
        };
      }
    };
  }
}
