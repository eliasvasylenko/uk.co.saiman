package uk.co.saiman.chemistry.msapex;

import java.util.Optional;

import uk.co.saiman.chemistry.Chemical;

/**
 * Request user input of a chemical.
 * 
 * @author Elias N Vasylenko
 */
public interface ChemicalSelectionRequester {
	/**
	 * @return a chemical selection from user input
	 */
	Optional<Chemical> requestChemical();
}
