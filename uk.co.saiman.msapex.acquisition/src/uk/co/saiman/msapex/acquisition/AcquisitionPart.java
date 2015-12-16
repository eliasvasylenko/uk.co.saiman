/*
 * Copyright (C) 2015 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.fx.ui.di.FXMLLoader;
import org.eclipse.fx.ui.di.FXMLLoaderFactory;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.instrument.acquisition.AcquisitionModule;
import uk.co.saiman.msapex.data.DataChartController;

public class AcquisitionPart {
	static class AcquisitionModuleCell extends ListCell<AcquisitionModule> {
		@Override
		protected void updateItem(AcquisitionModule item, boolean empty) {
			if (!empty && item != null) {
				setText(item.getName());
			}
			super.updateItem(item, empty);
		}
	}

	@Inject
	IEclipseContext context;

	@Inject
	@AcquisitionModules
	ObservableList<AcquisitionModule> acquisitionModules;

	private DataChartController chartController;

	@PostConstruct
	void init(BorderPane pane, @FXMLLoader FXMLLoaderFactory factory) {
		try {
			chartController = (DataChartController) factory.loadRequestorRelative("/uk/co/saiman/msapex/data/DataChart.fxml")
					.loadWithController().getController();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}

		pane.setCenter(chartController.getRootPane());
	}

	@Focus
	void focus() {}
}
