package uk.co.saiman.experiment;

import java.lang.reflect.Type;
import java.util.Map;

public interface MissingExperimentType extends ExperimentType<Map<String, String>> {
  @Override
  default Type getThisType() {
    return MissingExperimentType.class;
  }

  @Override
  default String getID() {
    return MissingExperimentType.class.getName();
  }
}
