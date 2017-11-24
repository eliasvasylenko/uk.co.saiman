package uk.co.saiman.experiment.spectrum;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import uk.co.saiman.data.spectrum.SpectrumProcessor;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.property.Property;

public class ConvolutionSpectrumProcessorConfiguration implements SpectrumProcessorConfiguration {
  private static final String VECTOR_KEY = "vector";
  private static final double[] NO_OP = new double[] { 1 };

  private final Property<double[]> vector;

  protected ConvolutionSpectrumProcessorConfiguration(PersistedState state) {
    vector = state
        .forString(VECTOR_KEY)
        .map(
            s -> stream(s.split(",")).mapToDouble(Double::parseDouble).toArray(),
            d -> stream(d).mapToObj(Double::toString).collect(joining(",")))
        .setDefault(() -> NO_OP);
  }

  public double[] getConvolutionVector() {
    return vector.get();
  }

  public void setConvolutionVector(double[] vector) {
    this.vector.set(vector);
  }

  @Override
  public SpectrumProcessor getProcessor() {
    double[] vector = this.vector.get();

    return new SpectrumProcessor() {
      @Override
      public double[] process(double[] data) {
        double[] processed = data.clone();

        return processed;
      }
    };
  }
}