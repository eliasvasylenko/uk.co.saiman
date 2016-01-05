package uk.co.saiman.msapex.acquisition;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.fx.core.di.Service;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import uk.co.saiman.instrument.acquisition.AcquisitionModule;

public class DevicesMenu {
	private class DeviceMenuItemOpen {
		private AcquisitionModule module;
		private MDirectMenuItem menuItem;

		public DeviceMenuItemOpen(AcquisitionModule module, MDirectMenuItem menuItem) {
			this.module = module;
			this.menuItem = menuItem;
		}

		@Execute
		public void execute() {
			if (menuItem.isSelected()) {
				acquisitionPart.addAcquisitionModule(module);
			} else {
				acquisitionPart.removeAcquisitionModule(module);
			}
		}
	}

	@Inject
	@Service
	List<AcquisitionModule> acquisitionModules;

	private AcquisitionPart acquisitionPart;

	@PostConstruct
	void initialise(EPartService partService) {
		acquisitionPart = (AcquisitionPart) partService.findPart("uk.co.saiman.msapex.acquisition.part").getObject();
	}

	@Execute
	static void execute() {
		new Alert(AlertType.INFORMATION, "Hello there").showAndWait();
	}

	@AboutToShow
	void aboutToShow(List<MMenuElement> items) {
		try {
			for (AcquisitionModule module : new ArrayList<>(acquisitionModules)) {
				MDirectMenuItem moduleItem = MMenuFactory.INSTANCE.createDirectMenuItem();
				moduleItem.setLabel(module.getName());
				moduleItem.setType(ItemType.CHECK);
				moduleItem.setObject(new DeviceMenuItemOpen(module, moduleItem));
				moduleItem.setSelected(acquisitionPart.getSelectedAcquisitionModules().contains(module));
				items.add(moduleItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
