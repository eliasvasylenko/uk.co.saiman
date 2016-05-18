package uk.co.saiman.msapex.experiment;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentText;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.fx.TreeItemData;

/**
 * Remove a node from an experiment in the workspace
 * 
 * @author Elias N Vasylenko
 */
public class RemoveNode {
	@Execute
	void execute(ExecutionEvent event, @Localize ExperimentText text, MPart part) {
		ExperimentPart experimentPart = (ExperimentPart) part.getObject();
		TreeItemData<?> itemData = experimentPart.getExperimentTreeController().getSelection();

		if (!(itemData.getData() instanceof ExperimentNode<?>)) {
			throw new ExperimentException(text.illegalContextMenuFor(itemData.getData()));
		}

		ExperimentNode<?> selectedNode = (ExperimentNode<?>) itemData.getData();

		selectedNode.remove();
	}
}
