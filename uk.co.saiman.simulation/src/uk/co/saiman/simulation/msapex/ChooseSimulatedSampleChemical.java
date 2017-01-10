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
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.msapex;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import uk.co.saiman.chemistry.msapex.ChemicalSelectionRequester;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.msapex.ExperimentPart;
import uk.co.saiman.simulation.SimulationProperties;
import uk.co.saiman.simulation.msapex.treecontributions.ChemicalColor;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.fx.TreeItemData;

/**
 * Add an experiment to the workspace
 * 
 * @author Elias N Vasylenko
 */
public class ChooseSimulatedSampleChemical {
	/**
	 * The ID of the command in the e4 model fragment.
	 */
	public static final String COMMAND_ID = "uk.co.saiman.simulation.msapex.command.choosesimulatedsamplechemical";

	@Inject
	@Localize
	SimulationProperties text;

	@Inject
	ChemicalSelectionRequester chemicalUserSelectionRequest;

	@Execute
	synchronized void execute(MPart part) throws IOException {
		ExperimentPart experimentPart = (ExperimentPart) part.getObject();
		TreeItemData<?> itemData = experimentPart.getExperimentTreeController().getSelectionData();

		if (!(itemData.data() instanceof ChemicalColor)) {
			throw new ExperimentException(
					text.experiment().exception().illegalCommandForSelection(COMMAND_ID, itemData.data()));
		}

		ChemicalColor chemicalColor = (ChemicalColor) itemData.data();

		chemicalUserSelectionRequest.requestChemical().ifPresent(chemicalColor::setChemical);

		experimentPart.getExperimentTreeController().getTreeView().refresh();
	}
}
