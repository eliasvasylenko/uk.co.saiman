package uk.co.saiman.experiment.processing;

import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.processing.GaussianSmooth.State;
import uk.co.saiman.property.Property;
import uk.co.saiman.text.properties.PropertyLoader;

@Component
public class GaussianSmooth implements ProcessorType<State> {
  private static final String STANDARD_DEVIATION_KEY = "standardDeviation";
  private static final int BOX_ITERATIONS = 5;

  @Reference
  PropertyLoader propertyLoader;

  @Override
  public String getName() {
    return propertyLoader.getProperties(ProcessingProperties.class).gaussianSmoothProcessor().get();
  }

  @Override
  public State configure(PersistedState state) {
    return new State(state);
  }

  public class State extends ProcessorState {
    private final Property<Double> standardDeviation;

    public State(PersistedState state) {
      super(GaussianSmooth.this, state);
      standardDeviation = state
          .forString(STANDARD_DEVIATION_KEY)
          .map(Double::parseDouble, Object::toString)
          .setDefault(() -> 10d);
    }

    public double getStandardDeviation() {
      return standardDeviation.get();
    }

    public void setStandardDeviation(double standardDeviation) {
      this.standardDeviation.set(standardDeviation);
    }

    @Override
    public DataProcessor getProcessor() {
      /*
       * This is a little dense to properly document in place. For more information,
       * the implementation is based on the report "Fast Almost-Gaussian Filtering" by
       * Peter Kovesi.
       */

      double stdDevSquared12 = (getStandardDeviation() * getStandardDeviation()) * 12d;
      double idealBoxWidth = sqrt((stdDevSquared12 / BOX_ITERATIONS) + 1);

      int lowerBoxWidth = (int) floor(idealBoxWidth / 2) * 2 - 1;
      int lowerIterations = (int) ((BOX_ITERATIONS * (lowerBoxWidth * (lowerBoxWidth + 4) + 3)
          - stdDevSquared12) / (4 * lowerBoxWidth + 4));

      int higherBoxWidth = lowerBoxWidth + 2;
      int higherIterations = BOX_ITERATIONS - lowerIterations;

      return DataProcessor.arrayProcessor(data -> {
        data = data.clone();

        for (int i = 0; i < lowerIterations; i++)
          BoxFilter.apply(data, lowerBoxWidth);

        for (int i = 0; i < higherIterations; i++)
          BoxFilter.apply(data, higherBoxWidth);

        return data;
      }, 0);
    }
  }
}
