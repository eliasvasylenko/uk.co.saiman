package uk.co.saiman.experiment.spectrum;

import uk.co.saiman.data.spectrum.SpectrumProcessor;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.utility.Copyable;

public abstract class SpectrumProcessorState implements Copyable<SpectrumProcessorState> {
  public static final String PROCESSING_KEY = "processing";
  public static final String PROCESSOR_TYPE_KEY = "type";

  private final SpectrumProcessorType<?> type;
  private final PersistedState state;

  public SpectrumProcessorState(SpectrumProcessorType<?> type, PersistedState state) {
    this.type = type;
    this.state = state;

    this.state.forString(PROCESSOR_TYPE_KEY).set(type.getId());
  }

  public PersistedState getPersistedState() {
    return state;
  }

  public SpectrumProcessorType<?> getProcessorType() {
    return type;
  }

  public abstract SpectrumProcessor getProcessor();

  @Override
  public SpectrumProcessorState copy() {
    return getProcessorType().configure(getPersistedState().copy());
  }
}
