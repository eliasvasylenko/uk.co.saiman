/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.chemistry.msapex.
 *
 * uk.co.saiman.chemistry.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.chemistry.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.chemistry.msapex.impl;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import uk.co.saiman.chemistry.PeriodicTable;
import uk.co.saiman.chemistry.msapex.PeriodicTableService;

/**
 * Track periodic tables available through OSGi services and select which table
 * to display in the periodic table part.
 * 
 * @author Elias N Vasylenko
 */
public class PeriodicTablesMenu {
	@Inject
	PeriodicTableService periodicTables;

	@Execute
	void execute() {
		PeriodicTable periodicTable = periodicTables.periodicTable().get();
		if (periodicTable == null) {
			new Alert(AlertType.INFORMATION, "No Periodic Table").showAndWait();
		} else {
			new Alert(AlertType.INFORMATION, "Periodic Table: " + periodicTable.getName()).showAndWait();
		}
	}
}
