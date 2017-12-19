package uk.co.saiman.experiment.spectrum;

import uk.co.saiman.data.spectrum.SpectrumProcessor;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.persistence.PersistedState;

public class MissingSpectrumProcessorType implements SpectrumProcessorType<SpectrumProcessorState> {
  private final String id;
  private final SpectrumProperties text;

  public MissingSpectrumProcessorType(String id, SpectrumProperties text) {
    this.id = id;
    this.text = text;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return text.missingSpectrumProcessor().get();
  }

  @Override
  public SpectrumProcessorState configure(PersistedState state) {
    return new SpectrumProcessorState(MissingSpectrumProcessorType.this, state) {
      @Override
      public SpectrumProcessor getProcessor() {
        throw new ExperimentException(text.exception().cannotFindSpectrumProcessor(id));
      }
    };
  }
}
