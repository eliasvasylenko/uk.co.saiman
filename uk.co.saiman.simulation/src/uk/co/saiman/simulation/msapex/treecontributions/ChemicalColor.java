package uk.co.saiman.simulation.msapex.treecontributions;

import java.util.function.Consumer;

import uk.co.saiman.chemistry.ChemicalComposition;
import uk.co.strangeskies.text.properties.Localized;

class ChemicalColor {
	private final Localized<String> name;
	private ChemicalComposition chemical;
	private final Consumer<ChemicalComposition> setChemical;

	public ChemicalColor(Localized<String> name, ChemicalComposition chemical,
			Consumer<ChemicalComposition> setChemical) {
		this.name = name;
		this.chemical = chemical;
		this.setChemical = setChemical;
	}

	public Localized<String> getName() {
		return name;
	}

	public ChemicalComposition getChemical() {
		return chemical;
	}

	public void getChemical(ChemicalComposition chemical) {
		setChemical.accept(chemical);
		this.chemical = chemical;
	}
}