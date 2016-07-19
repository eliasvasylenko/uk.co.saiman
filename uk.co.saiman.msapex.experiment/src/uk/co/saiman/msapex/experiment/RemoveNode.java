/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.fx.TreeItemData;

/**
 * Remove a node from an experiment in the workspace
 * 
 * @author Elias N Vasylenko
 */
public class RemoveNode {
	@Execute
	void execute(MPart part, @Localize ExperimentProperties text) {
		ExperimentPart experimentPart = (ExperimentPart) part.getObject();
		TreeItemData<?> itemData = experimentPart.getExperimentTreeController().getSelectionData();

		if (!(itemData.getData() instanceof ExperimentNode<?, ?>)) {
			throw new ExperimentException(text.exception().illegalContextMenuFor(itemData.getData()));
		}

		ExperimentNode<?, ?> selectedNode = (ExperimentNode<?, ?>) itemData.getData();
		selectedNode.remove();

		experimentPart.getExperimentTreeController().refresh();
	}
}
