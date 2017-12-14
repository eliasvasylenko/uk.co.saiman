package uk.co.saiman.experiment.spectrum;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.text.properties.PropertyLoader;

public class VectorConvolutionSpectrumProcessor extends ConvolutionSpectrumProcessor
    implements SpectrumProcessorType {
  private static final String VECTOR_KEY = "vector";

  @Reference
  PropertyLoader propertyLoader;

  public VectorConvolutionSpectrumProcessor() {}

  protected VectorConvolutionSpectrumProcessor(double[] vector) {
    super(vector);
  }

  @Override
  public String getName() {
    return propertyLoader.getProperties(SpectrumProperties.class).convolutionProcessor().get();
  }

  @Override
  public VectorConvolutionSpectrumProcessor load(PersistedState state) {
    return new VectorConvolutionSpectrumProcessor(
        stream(state.forString(VECTOR_KEY).get().split(","))
            .mapToDouble(Double::parseDouble)
            .toArray());
  }

  public void save(PersistedState state) {
    state
        .forString(VECTOR_KEY)
        .set(stream(getConvolutionVector()).mapToObj(Double::toString).collect(joining(",")));
  }
}
