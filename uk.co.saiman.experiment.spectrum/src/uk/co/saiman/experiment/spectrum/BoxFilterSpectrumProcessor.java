package uk.co.saiman.experiment.spectrum;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.spectrum.SpectrumProcessor;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.text.properties.PropertyLoader;

@Component
public class BoxFilterSpectrumProcessor implements SpectrumProcessorType {
  private static final String WIDTH_KEY = "width";
  protected static final int NO_OP = 1;

  @Reference
  PropertyLoader propertyLoader;

  private final int width;

  public BoxFilterSpectrumProcessor() {
    width = NO_OP;
  }

  public BoxFilterSpectrumProcessor(int width) {
    this.width = width;
  }

  @Override
  public String getId() {
    return getClass().getName();
  }

  @Override
  public String getName() {
    return propertyLoader.getProperties(SpectrumProperties.class).boxFilterProcessor().get();
  }

  public int getWidth() {
    return width;
  }

  @Override
  public BoxFilterSpectrumProcessor load(PersistedState state) {
    return new BoxFilterSpectrumProcessor(Integer.parseInt(state.forString(WIDTH_KEY).get()));
  }

  public void save(PersistedState state) {
    state.forString(WIDTH_KEY).set(Integer.toString(width));
  }

  public static void apply(double[] data, int boxWidth) {
    // TODO Auto-generated method stub

  }

  @Override
  public SpectrumProcessor getProcessor() {
    int width = this.width;

    return new SpectrumProcessor() {
      @Override
      public double[] process(double[] data) {
        data = data.clone();
        apply(data, width);
        return data;
      }
    };
  }
}
