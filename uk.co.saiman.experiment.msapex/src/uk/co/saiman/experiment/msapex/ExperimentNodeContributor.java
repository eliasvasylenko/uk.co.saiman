/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex;

import static java.util.stream.Stream.concat;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.osgi.service.component.annotations.Component;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentResult;
import uk.co.saiman.experiment.RootExperiment;
import uk.co.strangeskies.eclipse.EclipseModularTreeContributor;
import uk.co.strangeskies.eclipse.EclipseModularTreeContributorImpl;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.eclipse.MenuTreeCellContribution;
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
@Component(service = EclipseModularTreeContributor.class)
public class ExperimentNodeContributor extends EclipseModularTreeContributorImpl {
	@SuppressWarnings("javadoc")
	public ExperimentNodeContributor() {
		super(RootExperimentNodeContribution.class, ExperimentNodeContribution.class, ExperimentResultContribution.class);
	}
}

/**
 * Contribution for root experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
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
		return "[" + text.lifecycleState(data.data().lifecycleState().get()) + "]";
	}
}

/**
 * Contribution for root experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
class ExperimentResultContribution
		implements TreeTextContribution<ExperimentResult<?, ?>>, PseudoClassTreeCellContribution<ExperimentResult<?, ?>> {
	@Inject
	@Localize
	ExperimentProperties text;

	@Override
	public <U extends ExperimentResult<?, ?>> String getText(TreeItemData<U> data) {
		return data.data().getResultType().getName();
	}

	@Override
	public <U extends ExperimentResult<?, ?>> String getSupplementalText(TreeItemData<U> data) {
		return "[" + data.data().getData() + "]";
	}
}

/**
 * Contribution for all experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
class ExperimentNodeContribution extends MenuTreeCellContribution<ExperimentNode<?, ?>>
		implements PseudoClassTreeCellContribution<ExperimentNode<?, ?>>, TreeChildContribution<ExperimentNode<?, ?>>,
		TreeTextContribution<ExperimentNode<?, ?>> {
	private static final String EXPERIMENT_TREE_POPUP_MENU = "uk.co.saiman.experiment.msapex.popupmenu.node";

	public ExperimentNodeContribution() {
		super(EXPERIMENT_TREE_POPUP_MENU);
	}

	@Override
	public <U extends ExperimentNode<?, ?>> Node configureCell(TreeItemData<U> data, Node content) {
		data.data().lifecycleState().addWeakObserver(data, d -> s -> d.refresh(false));

		/*
		 * label to show lifecycle state icon
		 */
		Label lifecycleIndicator = new Label();
		lifecycleIndicator.pseudoClassStateChanged(
				PseudoClass.getPseudoClass(
						ExperimentLifecycleState.class.getSimpleName() + "-" + data.data().lifecycleState().get().toString()),
				true);

		/*
		 * shift lifecycle label to the far right
		 */
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		spacer.setMinWidth(Region.USE_PREF_SIZE);

		content = new HBox(content, spacer, lifecycleIndicator);

		content = PseudoClassTreeCellContribution.super.configureCell(data, content);

		return super.configureCell(data, content);
	}

	@Override
	public <U extends ExperimentNode<?, ?>> boolean hasChildren(TreeItemData<U> data) {
		return data.data().getChildren().findAny().isPresent() || data.data().getResults().findAny().isPresent();
	}

	@Override
	public <U extends ExperimentNode<?, ?>> List<TypedObject<?>> getChildren(TreeItemData<U> data) {
		return concat(data.data().getChildren(), data.data().getResults()).map(Reified::asTypedObject)
				.collect(Collectors.toList());
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
