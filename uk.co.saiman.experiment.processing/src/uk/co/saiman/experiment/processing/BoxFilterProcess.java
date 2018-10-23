package uk.co.saiman.experiment.processing;

import static uk.co.saiman.experiment.state.Accessor.intAccessor;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.function.processing.BoxFilter;
import uk.co.saiman.experiment.state.Accessor.PropertyAccessor;
import uk.co.saiman.experiment.state.StateMap;

@Component
public class BoxFilterProcess implements ProcessingStrategy<BoxFilter> {
  private static final PropertyAccessor<Integer> WIDTH = intAccessor("width");

  @Override
  public BoxFilter createProcessor() {
    return new BoxFilter();
  }

  @Override
  public BoxFilter configureProcessor(StateMap state) {
    return new BoxFilter(state.getOptional(WIDTH).orElse(BoxFilter.NO_OP));
  }

  @Override
  public StateMap deconfigureProcessor(BoxFilter processor) {
    return StateMap.empty().with(WIDTH, processor.getWidth());
  }

  @Override
  public Class<BoxFilter> getType() {
    return BoxFilter.class;
  }
}
