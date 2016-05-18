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
import uk.co.saiman.experiment.ExperimentText;
import uk.co.saiman.experiment.ExperimentWorkspace;
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
	private TreeItemType<ExperimentNode<ExperimentConfiguration>> rootExperimentNodeItemType;

	@Inject
	@Localize
	ExperimentText text;

	@Inject
	EMenuService menuService;

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
		rootExperimentNodeItemType = new ExperimentNodeTreeItemType<ExperimentConfiguration>(
				workspace.getRootExperimentType(), menuService) {
			@Override
			public String getText(ExperimentNode<ExperimentConfiguration> data) {
				return data.getState().getName();
			}

			@Override
			public String getSupplementalText(ExperimentNode<ExperimentConfiguration> data) {
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

	public TreeItemData<?> getSelection() {
		return treeView.getSelectionModel().getSelectedItem().getValue();
	}
}
