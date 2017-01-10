/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.instrument.acquisition.msapex.
 *
 * uk.co.saiman.instrument.acquisition.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.acquisition.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.acquisition.msapex;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

import uk.co.saiman.acquisition.AcquisitionDevice;

/**
 * Track acquisition devices available through OSGi services and select which
 * device to display in the acquisition part.
 * 
 * @author Elias N Vasylenko
 */
public class AcquisitionDevicesMenu {
	private AcquisitionPart acquisitionPart;

	@PostConstruct
	void initialise(MPart part) {
		acquisitionPart = (AcquisitionPart) part.getObject();
	}

	@AboutToShow
	void aboutToShow(List<MMenuElement> items) {
		for (AcquisitionDevice module : new ArrayList<>(acquisitionPart.getAvailableAcquisitionDevices())) {
			MDirectMenuItem moduleItem = MMenuFactory.INSTANCE.createDirectMenuItem();
			moduleItem.setLabel(module.getName());
			moduleItem.setType(ItemType.CHECK);
			moduleItem.setSelected(acquisitionPart.getSelectedAcquisitionDevices().contains(module));
			moduleItem.setObject(new Object() {
				@Execute
				public void execute() {
					if (moduleItem.isSelected()) {
						acquisitionPart.selectAcquisitionDevice(module);
					} else {
						acquisitionPart.deselectAcquisitionDevice(module);
					}
				}
			});

			items.add(moduleItem);
		}
	}
}
