package uk.co.saiman.experiment.processing;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.utility.Copyable;

public abstract class ProcessorState implements Copyable<ProcessorState> {
  public static final String PROCESSING_KEY = "processing";
  public static final String PROCESSOR_TYPE_KEY = "type";

  private final ProcessorType<?> type;
  private final PersistedState state;

  public ProcessorState(ProcessorType<?> type, PersistedState state) {
    this.type = type;
    this.state = state;

    this.state.forString(PROCESSOR_TYPE_KEY).set(type.getId());
  }

  public PersistedState getPersistedState() {
    return state;
  }

  public ProcessorType<?> getProcessorType() {
    return type;
  }

  public abstract DataProcessor getProcessor();

  @Override
  public ProcessorState copy() {
    return getProcessorType().configure(getPersistedState().copy());
  }
}
