package uk.co.saiman.experiment.spectrum;

import static java.lang.Double.parseDouble;
import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.spectrum.SpectrumProcessor;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.text.properties.PropertyLoader;

@Component
public class GaussianSmoothSpectrumProcessor implements SpectrumProcessorType {
  private static final String STANDARD_DEVIATION_KEY = "standardDeviation";
  private static final int BOX_ITERATIONS = 5;

  @Reference
  PropertyLoader propertyLoader;

  private final double standardDeviation;

  private final int lowerBoxWidth;
  private final int lowerIterations;
  private final int higherBoxWidth;
  private final int higherIterations;

  public GaussianSmoothSpectrumProcessor() {
    this(10);
  }

  protected GaussianSmoothSpectrumProcessor(double standardDeviation) {
    this.standardDeviation = standardDeviation;

    /*
     * This is a little dense to properly document in place. For more information,
     * the implementation is based on the report "Fast Almost-Gaussian Filtering" by
     * Peter Kovesi.
     */

    double stdDevSquared12 = (standardDeviation * standardDeviation) * 12d;
    double idealBoxWidth = sqrt((stdDevSquared12 / BOX_ITERATIONS) + 1);

    lowerBoxWidth = (int) floor(idealBoxWidth / 2) * 2 - 1;
    lowerIterations = (int) ((BOX_ITERATIONS * (lowerBoxWidth * (lowerBoxWidth + 4) + 3)
        - stdDevSquared12) / (4 * lowerBoxWidth + 4));

    higherBoxWidth = lowerBoxWidth + 2;
    higherIterations = BOX_ITERATIONS - lowerIterations;
  }

  @Override
  public String getId() {
    return getClass().getName();
  }

  @Override
  public String getName() {
    return propertyLoader.getProperties(SpectrumProperties.class).gaussianSmoothProcessor().get();
  }

  @Override
  public GaussianSmoothSpectrumProcessor load(PersistedState state) {
    return new GaussianSmoothSpectrumProcessor(
        parseDouble(state.forString(STANDARD_DEVIATION_KEY).get()));
  }

  public void save(PersistedState state) {
    state.forString(STANDARD_DEVIATION_KEY).set(Double.toString(standardDeviation));
  }

  @Override
  public SpectrumProcessor getProcessor() {
    return new SpectrumProcessor() {
      @Override
      public double[] process(double[] data) {
        data = data.clone();

        for (int i = 0; i < lowerIterations; i++)
          BoxFilterSpectrumProcessor.apply(data, lowerBoxWidth);

        for (int i = 0; i < higherIterations; i++)
          BoxFilterSpectrumProcessor.apply(data, higherBoxWidth);

        return data;
      }
    };
  }
}
