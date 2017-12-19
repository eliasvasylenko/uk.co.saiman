package uk.co.saiman.experiment.spectrum;

import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.spectrum.SpectrumProcessor;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.spectrum.SpectrumGaussianSmooth.State;
import uk.co.saiman.property.Property;
import uk.co.saiman.text.properties.PropertyLoader;

@Component
public class SpectrumGaussianSmooth implements SpectrumProcessorType<State> {
  private static final String STANDARD_DEVIATION_KEY = "standardDeviation";
  private static final int BOX_ITERATIONS = 5;

  @Reference
  PropertyLoader propertyLoader;

  @Override
  public String getName() {
    return propertyLoader.getProperties(SpectrumProperties.class).gaussianSmoothProcessor().get();
  }

  @Override
  public State configure(PersistedState state) {
    return new State(state);
  }

  class State extends SpectrumProcessorState {
    private final Property<Double> standardDeviation;

    public State(PersistedState state) {
      super(SpectrumGaussianSmooth.this, state);
      standardDeviation = state
          .forString(STANDARD_DEVIATION_KEY)
          .map(Double::parseDouble, Object::toString)
          .setDefault(() -> 10d);
    }

    public double getStandardDeviation() {
      return standardDeviation.get();
    }

    @Override
    public SpectrumProcessor getProcessor() {
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

      return new SpectrumProcessor() {
        @Override
        public double[] process(double[] data) {
          data = data.clone();

          for (int i = 0; i < lowerIterations; i++)
            SpectrumBoxFilter.apply(data, lowerBoxWidth);

          for (int i = 0; i < higherIterations; i++)
            SpectrumBoxFilter.apply(data, higherBoxWidth);

          return data;
        }
      };
    }
  }
}
