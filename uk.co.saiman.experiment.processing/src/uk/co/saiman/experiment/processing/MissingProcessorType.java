package uk.co.saiman.experiment.processing;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.persistence.PersistedState;

public class MissingProcessorType implements ProcessorType<ProcessorState> {
  private final String id;
  private final ProcessingProperties text;

  public MissingProcessorType(String id, ProcessingProperties text) {
    this.id = id;
    this.text = text;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return text.missingProcessor().get();
  }

  @Override
  public ProcessorState configure(PersistedState state) {
    return new ProcessorState(MissingProcessorType.this, state) {
      @Override
      public DataProcessor getProcessor() {
        throw new ExperimentException("Cannot find processor " + id);
      }
    };
  }
}
