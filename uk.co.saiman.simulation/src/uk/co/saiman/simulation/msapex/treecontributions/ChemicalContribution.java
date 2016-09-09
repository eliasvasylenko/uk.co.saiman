package uk.co.saiman.simulation.msapex.treecontributions;

import java.util.Objects;

import uk.co.strangeskies.fx.PseudoClassTreeCellContribution;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.fx.TreeTextContribution;

@SuppressWarnings("javadoc")
public class ChemicalContribution
		implements PseudoClassTreeCellContribution<ChemicalColor>, TreeTextContribution<ChemicalColor> {
	@Override
	public <U extends ChemicalColor> String getText(TreeItemData<U> data) {
		return data.data().getName().toString();
	}

	@Override
	public <U extends ChemicalColor> String getSupplementalText(TreeItemData<U> data) {
		return Objects.toString(data.data().getChemical());
	}
}