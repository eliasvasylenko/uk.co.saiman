package uk.co.saiman.experiment.spectrum;

import uk.co.saiman.data.spectrum.SpectrumProcessor;
import uk.co.saiman.experiment.persistence.PersistedState;

public abstract class ConvolutionSpectrumProcessor implements SpectrumProcessorType {
  protected static final double[] NO_OP = new double[] { 1 };

  private final double[] vector;

  public ConvolutionSpectrumProcessor() {
    vector = NO_OP;
  }

  public ConvolutionSpectrumProcessor(double[] vector) {
    this.vector = vector;
  }

  public double[] getConvolutionVector() {
    return vector.clone();
  }

  @Override
  public String getId() {
    return getClass().getName();
  }

  @Override
  public abstract ConvolutionSpectrumProcessor load(PersistedState state);

  @Override
  public SpectrumProcessor getProcessor() {
    double[] vector = this.vector;

    return new SpectrumProcessor() {
      @Override
      public double[] process(double[] data) {
        double[] processed = new double[data.length];

        return processed;
      }
    };
  }
}
