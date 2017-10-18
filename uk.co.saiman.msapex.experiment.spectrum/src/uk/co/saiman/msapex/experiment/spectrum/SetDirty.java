package uk.co.saiman.msapex.experiment.spectrum;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;

public class SetDirty {
  @Execute
  void execute(MDirtyable dirtyable) {
    dirtyable.setDirty(!dirtyable.isDirty());
    System.out.println("dirty? " + dirtyable.isDirty());
  }
}
