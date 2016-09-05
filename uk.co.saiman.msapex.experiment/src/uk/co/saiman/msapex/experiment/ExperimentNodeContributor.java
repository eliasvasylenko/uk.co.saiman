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

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.RootExperiment;
import uk.co.strangeskies.eclipse.EclipseTreeContribution;
import uk.co.strangeskies.eclipse.EclipseTreeContributionImpl;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.fx.PseudoClassTreeCellContribution;
import uk.co.strangeskies.fx.TreeCellContribution;
import uk.co.strangeskies.fx.TreeChildContribution;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.fx.TreeTextContribution;
import uk.co.strangeskies.reflection.Reified;
import uk.co.strangeskies.reflection.TypedObject;

/**
 * An implementation of {@link TreeCellContribution} which registers the
 * experiment tree pop-up menu from the experiment project model fragment.
 * 
 * @author Elias N Vasylenko
 */
@Component(service = EclipseTreeContribution.class)
public class ExperimentNodeContributor extends EclipseTreeContributionImpl {
	@SuppressWarnings("javadoc")
	public ExperimentNodeContributor() {
		super(RootExperimentNodeContribution.class, ExperimentNodeContribution.class);
	}
}

class RootExperimentNodeContribution
		implements TreeTextContribution<ExperimentNode<RootExperiment, ExperimentConfiguration>>,
		PseudoClassTreeCellContribution<ExperimentNode<RootExperiment, ExperimentConfiguration>> {
	@Inject
	@Localize
	ExperimentProperties text;

	@Override
	public <U extends ExperimentNode<RootExperiment, ExperimentConfiguration>> String getText(TreeItemData<U> data) {
		return data.data().getState().getName();
	}

	@Override
	public <U extends ExperimentNode<RootExperiment, ExperimentConfiguration>> String getSupplementalText(
			TreeItemData<U> data) {
		return "[" + text.lifecycleState(data.data().getLifecycleState()) + "]";
	}
}

class ExperimentNodeContribution implements PseudoClassTreeCellContribution<ExperimentNode<?, ?>>,
		TreeChildContribution<ExperimentNode<?, ?>>, TreeTextContribution<ExperimentNode<?, ?>> {
	private static final String EXPERIMENT_TREE_POPUP_MENU = "uk.co.saiman.msapex.experiment.popupmenu.node";

	private final EMenuService menuService;

	@Inject
	public ExperimentNodeContribution(EMenuService menuService) {
		this.menuService = menuService;
	}

	@Override
	public <U extends ExperimentNode<?, ?>> Node configureCell(TreeItemData<U> data, Node content) {
		PseudoClassTreeCellContribution.super.configureCell(data, content);

		Control contextMenu = new Control() {};
		menuService.registerContextMenu(contextMenu, EXPERIMENT_TREE_POPUP_MENU);

		content.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
			contextMenu.getContextMenu().show(content, event.getScreenX(), event.getScreenY());
			event.consume();
		});
		content.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			contextMenu.getContextMenu().hide();
		});

		return content;
	}

	@Override
	public <U extends ExperimentNode<?, ?>> boolean hasChildren(TreeItemData<U> data) {
		return !data.data().getChildren().isEmpty();
	}

	@Override
	public <U extends ExperimentNode<?, ?>> List<TypedObject<?>> getChildren(TreeItemData<U> data) {
		return data.data().getChildren().stream().map(Reified::asTypedObject).collect(toList());
	}

	@Override
	public <U extends ExperimentNode<?, ?>> String getText(TreeItemData<U> data) {
		return data.data().getType().getName();
	}

	@Override
	public <U extends ExperimentNode<?, ?>> String getSupplementalText(TreeItemData<U> data) {
		return data.data().toString();
	}
}
