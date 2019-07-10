package uk.co.saiman.experiment.processing;

import static uk.co.saiman.experiment.processing.Processing.toProcessing;
import static uk.co.saiman.state.Accessor.mapAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.state.Accessor.ListAccessor;
import uk.co.saiman.state.Accessor.MapAccessor;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;

public final class ProcessingAccess {
  private ProcessingAccess() {}

  private static final MapIndex<String> PROCESSOR_ID = new MapIndex<>(
      "uk.co.saiman.experiment.processor.id",
      stringAccessor());

  public static ListAccessor<Processing> processingAccessor(ProcessingService service) {
    return processorAccessor(service)
        .toStreamAccessor()
        .map(p -> p.collect(toProcessing()), Processing::steps);
  }

  public static MapAccessor<DataProcessor> processorAccessor(ProcessingService service) {
    return mapAccessor(
        state -> processorFromState(service, state),
        processor -> processorToState(service, processor));
  }

  private static DataProcessor processorFromState(ProcessingService service, StateMap state) {
    String id = state.get(PROCESSOR_ID);
    state = state.remove(PROCESSOR_ID);

    var strategy = service.findStrategy(id).get();

    if (strategy != null) {
      return strategy.configureProcessor(state);
    } else {
      return new MissingProcessor(id);
    }
  }

  @SuppressWarnings("unchecked")
  private static StateMap processorToState(ProcessingService service, DataProcessor processor) {
    var strategy = service.findStrategy(processor.getClass()).get();

    return ((ProcessingStrategy<DataProcessor>) strategy)
        .deconfigureProcessor(processor)
        .with(PROCESSOR_ID, processor.getClass().getName());
  }
}
