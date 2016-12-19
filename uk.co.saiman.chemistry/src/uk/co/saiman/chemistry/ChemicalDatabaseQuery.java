package uk.co.saiman.chemistry;

import java.util.Set;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

import uk.co.strangeskies.mathematics.Range;

/**
 * An interface providing a view over some chemical database.
 * 
 * @author Elias N Vasylenko
 */
public interface ChemicalDatabaseQuery {
	/**
	 * @return the set of chemicals which match the query
	 */
	Set<Chemical> findChemicals();

	ChemicalDatabaseQuery withMass(Quantity<Mass> mass, double relativeErrorMargin);

	ChemicalDatabaseQuery withMass(Range<Quantity<Mass>> massRange);

	ChemicalDatabaseQuery containingElements(ChemicalComposition composition);

	/**
	 * Structural information may only optionally be provided by a database, so by
	 * default this returns the empty query.
	 * 
	 * @return a derived query limiting to chemicals which contain the given
	 *         structure
	 */
	default ChemicalDatabaseQuery containingSubStructure(/*- TODO ChemicalStructure structure */) {
		return EmptyChemicalDatabaseQuery.instance();
	}
}
