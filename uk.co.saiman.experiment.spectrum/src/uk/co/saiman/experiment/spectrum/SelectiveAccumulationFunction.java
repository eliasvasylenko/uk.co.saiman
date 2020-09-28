package uk.co.saiman.experiment.spectrum;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import uk.co.saiman.data.function.ContinuousFunction;

public interface SelectiveAccumulationFunction {
  double score(ContinuousFunction<Time, Dimensionless> data);
}
