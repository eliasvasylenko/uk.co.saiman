package uk.co.saiman.experiment.processing;

import static uk.co.saiman.data.function.processing.Convolution.DomainModification.EXTENDING;
import static uk.co.saiman.experiment.state.Accessor.doubleAccessor;
import static uk.co.saiman.experiment.state.Accessor.intAccessor;

import java.util.stream.DoubleStream;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.function.processing.Convolution;
import uk.co.saiman.data.function.processing.Convolution.DomainModification;
import uk.co.saiman.experiment.state.Accessor;
import uk.co.saiman.experiment.state.StateMap;

@Component
public class ConvolutionProcess implements ProcessingStrategy<Convolution> {
  private static final Accessor<double[], ?> VECTOR = doubleAccessor("vector")
      .toStreamAccessor()
      .map(s -> s.mapToDouble(e -> e).toArray(), a -> DoubleStream.of(a).mapToObj(e -> e));
  private static final Accessor<Integer, ?> OFFSET = intAccessor("offset");
  private static final Accessor<Convolution.DomainModification, ?> DOMAIN_MODIFICATION = Accessor
      .stringAccessor("extend")
      .map(DomainModification::valueOf, Enum::name);

  @Override
  public Convolution createProcessor() {
    return new Convolution();
  }

  @Override
  public Convolution configureProcessor(StateMap state) {
    return new Convolution(
        state.getOptional(VECTOR).orElse(Convolution.NO_OP),
        state.getOptional(OFFSET).orElse(0),
        state.getOptional(DOMAIN_MODIFICATION).orElse(EXTENDING));
  }

  @Override
  public StateMap deconfigureProcessor(Convolution processor) {
    return StateMap
        .empty()
        .with(VECTOR, processor.getConvolutionVector())
        .with(OFFSET, processor.getConvolutionVectorOffset())
        .with(DOMAIN_MODIFICATION, processor.getDomainModification());
  }

  @Override
  public Class<Convolution> getType() {
    return Convolution.class;
  }
}
