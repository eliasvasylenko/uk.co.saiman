package uk.co.saiman.experiment.procedure;

import java.util.stream.Stream;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ProductIndex;

public interface ProcedureIndex extends ProductIndex {
  Stream<Procedure> procedures();

  Procedure result(String id);

  Instruction resolve(ExperimentPath path);
}
