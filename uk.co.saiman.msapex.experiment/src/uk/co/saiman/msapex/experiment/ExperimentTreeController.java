package uk.co.saiman.msapex.experiment;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentText;
import uk.co.saiman.experiment.ExperimentWorkspace;
import uk.co.strangeskies.eclipse.Localize;

public class ExperimentTreeController {
	@FXML
	private TreeView<TreeItemData<?>> treeView;

	private ExperimentWorkspace workspace;

	private final TreeItemType<ExperimentWorkspace> workspaceItemType;
	private TreeItemType<ExperimentNode<ExperimentConfiguration>> rootExperimentNodeItemType;

	@Inject
	@Localize
	ExperimentText text;

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
		treeView.setCellFactory(v -> new ExperimentTreeCell());
	}

	public void setWorkspace(ExperimentWorkspace workspace) {
		// prepare workspace
		this.workspace = workspace;

		// create root experiment node type
		rootExperimentNodeItemType = new ExperimentNodeTreeItemType<ExperimentConfiguration>(
				workspace.getRootExperimentType()) {
			@Override
			public String getText(ExperimentNode<ExperimentConfiguration> data) {
				return data.configuration().getName();
			}

			@Override
			public String getSupplementalText(ExperimentNode<ExperimentConfiguration> data) {
				return "[" + text.lifecycleState(data.lifecycleState()) + "]";
			}
		};

		// create root of experiment tree
		ExperimentTreeItem<ExperimentWorkspace> root = new ExperimentTreeItem<>(workspaceItemType, workspace);
		root.setExpanded(true);

		// add root
		treeView.setShowRoot(false);
		treeView.setRoot(root);
	}

	public void refresh() {
		((ExperimentTreeItem<?>) treeView.getRoot()).rebuildChildren();
	}
}
