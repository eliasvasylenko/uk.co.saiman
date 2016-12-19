package uk.co.saiman.chemistry;

import static java.util.Collections.emptySet;

import java.util.Set;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

import uk.co.strangeskies.mathematics.Range;

public class EmptyChemicalDatabaseQuery implements ChemicalDatabaseQuery {
	private static final ChemicalDatabaseQuery INSTANCE = new EmptyChemicalDatabaseQuery();

	private EmptyChemicalDatabaseQuery() {}

	@Override
	public ChemicalDatabaseQuery withMass(Range<Quantity<Mass>> massRange) {
		return this;
	}

	@Override
	public ChemicalDatabaseQuery withMass(Quantity<Mass> mass, double relativeErrorMargin) {
		return this;
	}

	@Override
	public ChemicalDatabaseQuery containingElements(ChemicalComposition composition) {
		return this;
	}

	@Override
	public Set<Chemical> findChemicals() {
		return emptySet();
	}

	public static ChemicalDatabaseQuery instance() {
		return INSTANCE;
	}
}
