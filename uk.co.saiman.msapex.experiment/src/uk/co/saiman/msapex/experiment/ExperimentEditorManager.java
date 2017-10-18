package uk.co.saiman.msapex.experiment;

import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;

import uk.co.saiman.experiment.Result;

public interface ExperimentEditorManager {
  <T> MCompositePart openEditor(Result<T> result);
}
