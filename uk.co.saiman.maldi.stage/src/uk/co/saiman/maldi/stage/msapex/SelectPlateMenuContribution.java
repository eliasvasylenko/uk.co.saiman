package uk.co.saiman.maldi.stage.msapex;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;

public class SelectPlateMenuContribution {
  @Inject
  private SamplePlatePresentationService presentationService;

  @AboutToShow
  void postConstruct(List<MMenuItem> menuItems) {
    presentationService.getPresenters().map(this::createMenuItem).forEach(menuItems::add);
  }

  MMenuItem createMenuItem(SamplePlatePresenter presenter) {
    MDirectMenuItem moduleItem = MMenuFactory.INSTANCE.createDirectMenuItem();
    moduleItem.setLabel(presenter.getLocalizedLabel());
    moduleItem.setIconURI(presenter.getIconURI());
    moduleItem.setType(ItemType.PUSH);
    moduleItem.setObject(new Object() {
      @Execute
      public void execute(IEclipseContext context) {
        presenter.present();
      }
    });
    return moduleItem;
  }
}
