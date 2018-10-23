package uk.co.saiman.experiment.processing;

import static uk.co.saiman.data.function.processing.GaussianSmooth.DEFAULT_STANDARD_DEVIATION;
import static uk.co.saiman.experiment.state.Accessor.doubleAccessor;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.function.processing.GaussianSmooth;
import uk.co.saiman.experiment.state.Accessor.PropertyAccessor;
import uk.co.saiman.experiment.state.StateMap;

@Component
public class GaussianSmoothProcess implements ProcessingStrategy<GaussianSmooth> {
  private static final PropertyAccessor<Double> STANDARD_DEVIATION = doubleAccessor(
      "standardDeviation");

  @Override
  public GaussianSmooth createProcessor() {
    return new GaussianSmooth();
  }

  @Override
  public GaussianSmooth configureProcessor(StateMap state) {
    return new GaussianSmooth(
        state.getOptional(STANDARD_DEVIATION).orElse(DEFAULT_STANDARD_DEVIATION));
  }

  @Override
  public StateMap deconfigureProcessor(GaussianSmooth processor) {
    return StateMap.empty().with(STANDARD_DEVIATION, processor.getStandardDeviation());
  }

  @Override
  public Class<GaussianSmooth> getType() {
    return GaussianSmooth.class;
  }
}
