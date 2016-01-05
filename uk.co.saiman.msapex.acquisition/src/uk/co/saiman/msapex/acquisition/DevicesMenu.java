/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.msapex.acquisition.
 *
 * uk.co.saiman.msapex.acquisition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.acquisition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
