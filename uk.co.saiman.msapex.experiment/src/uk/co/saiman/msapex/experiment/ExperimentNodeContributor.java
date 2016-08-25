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

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.services.EMenuService;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.strangeskies.fx.TreeCellContribution;
import uk.co.strangeskies.fx.TreeCellImpl;
import uk.co.strangeskies.fx.TreeChildContribution;
import uk.co.strangeskies.fx.TreeTextContribution;
import uk.co.strangeskies.reflection.Reified;
import uk.co.strangeskies.reflection.TypedObject;

/**
 * An implementation of {@link TreeCellContribution} which registers the
 * experiment tree pop-up menu from the experiment project model fragment.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class ExperimentNodeContributor implements ExperimentTreeContributor {
	@Override
	public Class<ExperimentNodeContribution> getContribution() {
		return ExperimentNodeContribution.class;
	}
}

class ExperimentNodeContribution implements TreeCellContribution<ExperimentNode<?, ?>>,
		TreeChildContribution<ExperimentNode<?, ?>>, TreeTextContribution<ExperimentNode<?, ?>> {
	private static final String EXPERIMENT_TREE_POPUP_MENU = "uk.co.saiman.msapex.experiment.popupmenu.node";

	private final EMenuService menuService;

	@Inject
	public ExperimentNodeContribution(EMenuService menuService) {
		this.menuService = menuService;
	}

	@Override
	public void configureCell(ExperimentNode<?, ?> data, String text, String supplementalText, TreeCellImpl cell) {
		TreeTextContribution.super.configureCell(data, text, supplementalText, cell);
		menuService.registerContextMenu(cell, EXPERIMENT_TREE_POPUP_MENU);
	}

	@Override
	public void deconfigureCell(TreeCellImpl cell) {
		TreeTextContribution.super.deconfigureCell(cell);
		cell.contextMenuProperty().set(null);
	}

	@Override
	public boolean hasChildren(ExperimentNode<?, ?> data) {
		return !data.getChildren().isEmpty();
	}

	@Override
	public List<TypedObject<?>> getChildren(ExperimentNode<?, ?> data) {
		return data.getChildren().stream().map(Reified::asTypedObject).collect(toList());
	}

	@Override
	public String getText(ExperimentNode<?, ?> data) {
		return data.getType().getName();
	}

	@Override
	public String getSupplementalText(ExperimentNode<?, ?> data) {
		return data.toString();
	}
}
