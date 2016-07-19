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

import org.eclipse.e4.ui.services.EMenuService;

import uk.co.strangeskies.eclipse.E4TreeItemType;
import uk.co.strangeskies.fx.TreeItemType;

/**
 * A partial implementation of {@link TreeItemType} which registers the
 * experiment tree pop-up menu from the experiment project model fragment.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of the object at the experiment tree node
 */
public abstract class ExperimentTreeItemType<T> extends E4TreeItemType<T> {
	private static final String EXPERIMENT_TREE_POPUP_MENU = "uk.co.saiman.msapex.experiment.popupmenu.node";

	public ExperimentTreeItemType(EMenuService menuService) {
		super(menuService, EXPERIMENT_TREE_POPUP_MENU);
	}
}
