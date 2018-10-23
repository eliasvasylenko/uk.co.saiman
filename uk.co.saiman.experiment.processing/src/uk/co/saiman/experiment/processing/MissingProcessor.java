package uk.co.saiman.experiment.processing;

import javax.measure.Quantity;

import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.ExperimentException;

public class MissingProcessor implements DataProcessor {
  private final String id;

  public MissingProcessor(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  public <UD extends Quantity<UD>, UR extends Quantity<UR>> SampledContinuousFunction<UD, UR> process(
      SampledContinuousFunction<UD, UR> data) {
    throw new ExperimentException("Cannot find processor " + id);
  }
}
