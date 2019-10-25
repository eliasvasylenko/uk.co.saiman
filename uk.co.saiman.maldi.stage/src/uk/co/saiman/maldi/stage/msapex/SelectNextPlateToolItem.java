package uk.co.saiman.maldi.stage.msapex;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;

public class SelectNextPlateToolItem {
  private MDirectToolItem toolItem;

  @Execute
  void nextSamplePlate(@Optional MDirectToolItem item) {
    System.out.println(" next " + item);
  }

  @CanExecute
  boolean stupidHack(@Optional MDirectToolItem item, @Optional SamplePlatePresenter presenter) {
    this.toolItem = item;
    setSamplePlate(presenter);
    return true;
  }

  @Inject
  void setSamplePlate(@Optional SamplePlatePresenter presenter) {
    if (toolItem != null && presenter != null) {
      toolItem.setLabel(presenter.getLocalizedLabel());
      toolItem.setIconURI(presenter.getIconURI());
    }
  }
}
