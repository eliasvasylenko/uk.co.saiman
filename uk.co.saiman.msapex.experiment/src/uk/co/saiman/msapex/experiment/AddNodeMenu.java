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

import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.fx.TreeItemImpl;

/**
 * Track acquisition devices available through OSGi services and select which
 * device to display in the acquisition part.
 * 
 * @author Elias N Vasylenko
 */
public class AddNodeMenu {
	/**
	 * The ID of the command in the e4 model fragment.
	 */
	public static final String MENU_ID = "uk.co.saiman.msapex.experiment.dynamicmenucontribution.addnode";

	@AboutToShow
	void aboutToShow(List<MMenuElement> items, @Localize ExperimentProperties text, MPart part) {
		ExperimentPart experimentPart = (ExperimentPart) part.getObject();
		TreeItemImpl<?> item = experimentPart.getExperimentTreeController().getSelection();
		Object data = item.getValue().data();

		if (!(data instanceof ExperimentNode<?, ?>)) {
			throw new ExperimentException(text.exception().illegalMenuForSelection(MENU_ID, data));
		}

		ExperimentNode<?, ?> selectedNode = (ExperimentNode<?, ?>) data;

		selectedNode.getAvailableChildExperimentTypes().forEach(childType -> {
			MDirectMenuItem moduleItem = MMenuFactory.INSTANCE.createDirectMenuItem();
			moduleItem.setLabel(childType.getName());
			moduleItem.setType(ItemType.PUSH);
			moduleItem.setObject(new Object() {
				@Execute
				public void execute() {
					selectedNode.addChild(childType);
					experimentPart.getExperimentTreeController().getTreeView().refresh();
					item.setExpanded(true);
				}
			});

			items.add(moduleItem);
		});
	}
}
