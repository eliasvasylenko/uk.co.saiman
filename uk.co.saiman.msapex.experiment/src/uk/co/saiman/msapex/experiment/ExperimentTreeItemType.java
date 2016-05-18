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
	private static String EXPERIMENT_TREE_POPUP_MENU = "uk.co.saiman.msapex.experiment.popupmenu.node";

	public ExperimentTreeItemType(EMenuService menuService) {
		super(menuService, EXPERIMENT_TREE_POPUP_MENU);
	}
}
