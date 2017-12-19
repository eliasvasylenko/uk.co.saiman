package uk.co.saiman.experiment.spectrum;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.spectrum.SpectrumProcessor;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.spectrum.SpectrumBoxFilter.State;
import uk.co.saiman.property.Property;
import uk.co.saiman.text.properties.PropertyLoader;

@Component
public class SpectrumBoxFilter implements SpectrumProcessorType<State> {
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
    return propertyLoader.getProperties(SpectrumProperties.class).boxFilterProcessor().get();
  }

  public static void apply(double[] data, int boxWidth) {
    // TODO Auto-generated method stub

  }

  @Override
  public State configure(PersistedState state) {
    return new State(state);
  }

  public class State extends SpectrumProcessorState {
    private final Property<Integer> width;

    public State(PersistedState state) {
      super(SpectrumBoxFilter.this, state);
      width = state
          .forString(WIDTH_KEY)
          .map(Integer::parseInt, Object::toString)
          .setDefault(() -> NO_OP);
    }

    public Integer getWidth() {
      return width.get();
    }

    public void setWidth(int width) {
      this.width.set(width);
    }

    @Override
    public SpectrumProcessor getProcessor() {
      int width = getWidth();

      return data -> {
        data = data.clone();
        apply(data, width);
        return data;
      };
    }
  }
}
