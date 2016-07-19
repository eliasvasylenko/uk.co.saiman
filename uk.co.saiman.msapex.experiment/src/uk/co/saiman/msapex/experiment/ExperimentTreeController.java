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

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.e4.ui.services.EMenuService;

import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentWorkspace;
import uk.co.saiman.experiment.RootExperiment;
import uk.co.strangeskies.eclipse.E4TreeCellImpl;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.fx.TreeItemImpl;
import uk.co.strangeskies.fx.TreeItemType;

public class ExperimentTreeController {
	@FXML
	private TreeView<TreeItemData<?>> treeView;

	private ExperimentWorkspace workspace;

	private final TreeItemType<ExperimentWorkspace> workspaceItemType;
	private ExperimentNodeTreeItemType<RootExperiment, ExperimentConfiguration> rootExperimentNodeItemType;

	@Inject
	@Localize
	private ExperimentProperties text;

	@Inject
	private EMenuService menuService;

	@Inject
	private Provider<E4TreeCellImpl> cellProvider;

	public ExperimentTreeController() {
		workspaceItemType = new TreeItemType<ExperimentWorkspace>() {
			@Override
			public boolean hasChildren(ExperimentWorkspace data) {
				return !data.getRootExperiments().isEmpty();
			}

			@Override
			public List<TreeItemData<?>> getChildren(ExperimentWorkspace data) {
				return data.getRootExperiments().stream().map(r -> new TreeItemData<>(rootExperimentNodeItemType, r))
						.collect(Collectors.toList());
			}
		};
	}

	@FXML
	void initialize() {
		treeView.setCellFactory(v -> cellProvider.get());
	}

	public void setWorkspace(ExperimentWorkspace workspace) {
		// prepare workspace
		this.workspace = workspace;

		// create root experiment node type
		rootExperimentNodeItemType = new ExperimentNodeTreeItemType<RootExperiment, ExperimentConfiguration>(
				workspace.getRootExperimentType(), menuService) {
			@Override
			public String getText(ExperimentNode<RootExperiment, ExperimentConfiguration> data) {
				return data.getState().getName();
			}

			@Override
			public String getSupplementalText(ExperimentNode<RootExperiment, ExperimentConfiguration> data) {
				return "[" + text.lifecycleState(data.getLifecycleState()) + "]";
			}
		};

		// create root of experiment tree
		TreeItemImpl<ExperimentWorkspace> root = workspaceItemType.getTreeItem(workspace);
		root.setExpanded(true);

		// add root
		treeView.setShowRoot(false);
		treeView.setRoot(root);
	}

	public void refresh() {
		((TreeItemImpl<?>) treeView.getRoot()).rebuildChildren();
	}

	public TreeItemImpl<?> getSelection() {
		return (TreeItemImpl<?>) treeView.getSelectionModel().getSelectedItem();
	}

	public TreeItemData<?> getSelectionData() {
		return getSelection().getValue();
	}
}
