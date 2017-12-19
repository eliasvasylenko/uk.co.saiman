package uk.co.saiman.experiment.spectrum;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.spectrum.SpectrumProcessor;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.spectrum.SpectrumVectorConvolution.State;
import uk.co.saiman.property.Property;
import uk.co.saiman.text.properties.PropertyLoader;

@Component
public class SpectrumVectorConvolution extends SpectrumConvolution<State>
    implements SpectrumProcessorType<State> {
  private static final String VECTOR_KEY = "vector";

  @Reference
  PropertyLoader propertyLoader;

  @Override
  public String getName() {
    return propertyLoader.getProperties(SpectrumProperties.class).convolutionProcessor().get();
  }

  @Override
  public State configure(PersistedState state) {
    return new State(state);
  }

  public class State extends SpectrumProcessorState {
    private final Property<double[]> vector;

    public State(PersistedState state) {
      super(SpectrumVectorConvolution.this, state);
      vector = state
          .forString(VECTOR_KEY)
          .map(
              v -> stream(v.split(",")).mapToDouble(Double::parseDouble).toArray(),
              v -> stream(v).mapToObj(Double::toString).collect(joining(",")))
          .setDefault(() -> NO_OP);
    }

    public void setConvolutionVector(double[] vector) {
      this.vector.set(vector);
    }

    public double[] getConvolutionVector() {
      return vector.get();
    }

    @Override
    public SpectrumProcessor getProcessor() {
      double[] vector = getConvolutionVector();
      return data -> process(vector, data);
    }
  }
}
