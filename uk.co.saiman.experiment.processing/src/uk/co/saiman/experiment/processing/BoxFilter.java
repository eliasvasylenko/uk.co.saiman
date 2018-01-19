package uk.co.saiman.experiment.processing;

import static java.util.Arrays.fill;
import static uk.co.saiman.data.function.processing.DataProcessor.arrayProcessor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.processing.BoxFilter.State;
import uk.co.saiman.property.Property;
import uk.co.saiman.text.properties.PropertyLoader;

@Component
public class BoxFilter implements ProcessorType<State> {
  private static final String WIDTH_KEY = "width";
  protected static final int NO_OP = 1;

  @Reference
  PropertyLoader propertyLoader;

  @Override
  public String getId() {
    return getClass().getName();
  }

  @Override
  public String getName() {
    return propertyLoader.getProperties(ProcessingProperties.class).boxFilterProcessor().get();
  }

  public static void apply(double[] data, int boxWidth) {
    if (boxWidth <= 0)
      throw new IllegalArgumentException();
    if (boxWidth == 1)
      return;

    int headWidth = boxWidth / 2;
    int tailWidth = boxWidth - headWidth - 1; // depends whether even or odd
    double[] headMemory = new double[headWidth];
    fill(headMemory, data[0]);

    double runningTotal = data[0] * headWidth;
    for (int i = 0; i < tailWidth; i++) {
      int index = i;
      if (index >= data.length)
        index = data.length - 1;
      runningTotal += data[index];
    }

    data[0] = runningTotal / boxWidth;

    for (int i = 1; i < data.length; i++) {
      int headIndex = i % headWidth;
      int tailIndex = i + tailWidth;
      if (tailIndex >= data.length)
        tailIndex = data.length - 1;

      runningTotal += data[tailIndex] - headMemory[headIndex];

      headMemory[headIndex] = data[i];
      data[i] = runningTotal / boxWidth;
    }
  }

  @Override
  public State configure(PersistedState state) {
    return new State(state);
  }

  public class State extends ProcessorState {
    private final Property<Integer> width;

    public State(PersistedState state) {
      super(BoxFilter.this, state);
      width = state
          .forString(WIDTH_KEY)
          .map(Integer::parseInt, Object::toString)
          .setDefault(() -> NO_OP);
    }

    public int getWidth() {
      return width.get();
    }

    public void setWidth(int width) {
      this.width.set(width);
    }

    @Override
    public DataProcessor getProcessor() {
      int width = getWidth();

      return arrayProcessor(data -> {
        data = data.clone();
        apply(data, width);
        return data;
      }, width / 2);
    }
  }
}
