package uk.co.saiman.instrument.stage.copley;

import uk.co.saiman.instrument.stage.StageProperties;
import uk.co.strangeskies.text.properties.Localized;

public interface CopleyStageProperties {
  StageProperties stage();

  Localized<String> name();
}
