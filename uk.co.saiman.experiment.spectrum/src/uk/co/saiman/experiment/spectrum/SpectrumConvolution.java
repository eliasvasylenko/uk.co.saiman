package uk.co.saiman.experiment.spectrum;

public abstract class SpectrumConvolution<T extends SpectrumProcessorState>
    implements SpectrumProcessorType<T> {
  protected static final double[] NO_OP = new double[] { 1 };

  public double[] process(double[] convolutionVector, double[] data) {
    // TODO
    throw new UnsupportedOperationException();
  }
}
