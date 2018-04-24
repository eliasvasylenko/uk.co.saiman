package uk.co.saiman.measurement.fx;

import static uk.co.saiman.measurement.Quantities.quantityFormat;
import static uk.co.saiman.measurement.Units.unitFormat;

import java.text.NumberFormat;

import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import javafx.util.StringConverter;
import uk.co.saiman.measurement.scalar.Scalar;

public interface QuantityFormatter {
  StringConverter<Number> quantityFormatter(Unit<?> unit);

  StringConverter<Number> quantityFormatter(Unit<?> unit, NumberFormat numberFormat);

  StringConverter<Unit<?>> unitFormatter();

  static QuantityFormatter defaultQuantityFormatter() {
    return new QuantityFormatter() {
      @Override
      public StringConverter<Number> quantityFormatter(Unit<?> unit) {
        return new StringConverter<Number>() {
          @Override
          public String toString(Number object) {
            return quantityFormat().format(new Scalar<>(unit, object));
          }

          @Override
          public Number fromString(String string) {
            Quantity<?> quantity = quantityFormat().parse(string);
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
            return quantityFormat()
                .withNumberFormat(numberFormat)
                .format(new Scalar<>(unit, object));
          }

          @Override
          public Number fromString(String string) {
            Quantity<?> quantity = quantityFormat().withNumberFormat(numberFormat).parse(string);
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
            return unitFormat().format(object);
          }

          @Override
          public Unit<?> fromString(String string) {
            return unitFormat().parse(string);
          }
        };
      }
    };
  }
}
