package uk.co.saiman.maldi.sampleplates;

import static java.util.stream.Collectors.toList;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.sampleplate.IndexedSamplePlate;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePlate;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePlateIndex;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.measurement.scalar.Scalar;

@Component(property = MaldiSamplePlateIndex.SAMPLE_PLATE_ID + "=" + Maldi96WellPlate.ID)
public class Maldi96WellPlate extends IndexedSamplePlate<MaldiSampleWell>
    implements MaldiSamplePlate {
  public static final String ID = "96well";

  private static final char FIRST_ROW = 'A';
  private static final char LAST_ROW = 'H';

  private static final int FIRST_COLUMN = 1;
  private static final int LAST_COLUMN = 12;

  private static final Quantity<Length> RADIUS = new Scalar<>(Units.metre().milli(), 1.5);

  private static final Quantity<Length> FIRST_ROW_OFFSET = new Scalar<>(
      Units.metre().milli(),
      -15.75);
  private static final Quantity<Length> FIRST_COLUMN_OFFSET = new Scalar<>(
      Units.metre().milli(),
      -24.75);
  private static final Quantity<Length> SPACING = new Scalar<>(Units.metre().milli(), 4.5);

  public Maldi96WellPlate() {
    super(createRows().collect(toList()));
  }

  private static Stream<MaldiSampleWell> createRows() {
    return IntStream
        .rangeClosed(FIRST_ROW, LAST_ROW)
        .mapToObj(Row::new)
        .flatMap(Maldi96WellPlate::createColumns);
  }

  private static Stream<MaldiSampleWell> createColumns(Row row) {
    return IntStream
        .rangeClosed(FIRST_COLUMN, LAST_COLUMN)
        .mapToObj(Column::new)
        .map(column -> createWell(column, row));
  }

  private static MaldiSampleWell createWell(Column column, Row row) {
    return new MaldiSampleWell(
        Character.toString(row.character()) + Integer.toString(column.number()),
        new XYCoordinate<>(column.offset(), row.offset()),
        RADIUS);
  }

  @Override
  public Optional<XYCoordinate<Length>> barcodeLocation() {
    return Optional.empty();
  }

  static class Row {
    private final int index;

    public Row(int character) {
      this.index = character - FIRST_ROW;
    }

    public int index() {
      return index;
    }

    public char character() {
      return (char) (index + FIRST_ROW);
    }

    public Quantity<Length> offset() {
      return FIRST_ROW_OFFSET.add(SPACING.multiply(index));
    }
  }

  static class Column {
    private final int index;

    public Column(int number) {
      this.index = number - FIRST_COLUMN;
    }

    public int index() {
      return index;
    }

    public int number() {
      return index + FIRST_COLUMN;
    }

    public Quantity<Length> offset() {
      return FIRST_COLUMN_OFFSET.add(SPACING.multiply(index));
    }
  }
}
