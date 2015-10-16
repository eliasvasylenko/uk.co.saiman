package uk.co.saiman.chemistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import java.util.function.Consumer;

import uk.co.saiman.chemistry.isotope.Isotope;

/**
 * Parser for molecular formulae given a periodic table. Currently supports 2
 * character elements only (i.e. none of the temporary named elements). Gives
 * warnings for bad capitalisations or mismatched bracket types, and exceptions
 * for all other anticipated input problems. Supports specification of specific
 * isotopes with, e.g. (12C) or ^12C for Carbon-12. Removes all whitespace as
 * well as any dots or dashes. Use ?[mass] for unknown component of known mass,
 * e.g. ?12.345 for an unknown set of elements with a total mass of 12.345Da.
 *
 * @author Elias Vasylenko
 *
 */
public class ChemicalFormulaParser {
	private final Collection<? extends Element> elements;

	public ChemicalFormulaParser(PeriodicTable elements) {
		this(elements.getElements());
	}

	public ChemicalFormulaParser(Collection<? extends Element> elements) {
		this.elements = new ArrayList<>(elements);
	}

	public Collection<? extends Element> getElements() {
		return elements;
	}

	public ChemicalComposition parse(String molecularFormula) {
		return parse(molecularFormula, w -> {});
	}

	public ChemicalComposition parse(String molecularFormula, Consumer<Exception> warningConsumer) {
		if (molecularFormula == null) {
			return null;
		}

		if (molecularFormula.isEmpty()) {
			warningConsumer.accept(new ChemicalFormulaParserException("Formula string is empty."));
			return new ChemicalComposition();
		}

		Vector<Character> alreadyWarned = new Vector<Character>();
		/*
		 * stack to hold current series' of consecutively encountered elements
		 * between brackets. Top of stack to be expanded after bracket closed then
		 * multiplied by any following number and added to lower level.
		 */
		Stack<ChemicalComposition> moleculeStack = new Stack<ChemicalComposition>();
		moleculeStack.push(new ChemicalComposition());
		// currently processing molecule on stack
		ChemicalComposition currentMolecule;

		// current isotope specifier, null if none
		Integer isotopeSpecifier = null;

		// current bracket stack (to check [] {} () match up properly)
		Stack<Character> bracketStack = new Stack<Character>();

		// charge of molecule
		int charge = 0;

		// current character index
		int index = 0;
		char currentChar;
		while (index < molecularFormula.length()) {
			currentChar = molecularFormula.charAt(index);

			if (Character.isLetter(currentChar)) {
				// encountered letter, get all consecutive letters
				String consecutiveElementString = Character.toString(currentChar);
				while (index < molecularFormula.length() - 1 && Character.isLetter(molecularFormula.charAt(index + 1))) {
					consecutiveElementString += Character.toString(molecularFormula.charAt(++index));
				}
				// parse consecutive letters
				Vector<Element> consecutiveElements;
				consecutiveElements = parseConsecutiveElements(molecularFormula, consecutiveElementString.toString(),
						warningConsumer);

				Isotope isotope = null;
				// specify first element as isotope if necessary
				if (isotopeSpecifier != null) {
					Element firstElement = consecutiveElements.firstElement();
					consecutiveElements.remove(0);
					isotope = firstElement.getIsotope(isotopeSpecifier);
					if (isotope == null) {
						throw new ChemicalFormulaParserException("Unknown isotope specified: \"" + firstElement.getName() + "-"
								+ isotopeSpecifier.toString() + "\" in \"" + molecularFormula + "\".");
					}
				}

				// new molecule from parsed consecutive letters
				ChemicalComposition subMolecule;

				// number of end element
				Integer count = null;

				// find any following consecutive digits
				if (index < molecularFormula.length() - 1 && Character.isDigit(molecularFormula.charAt(index + 1))) {
					String valueString = Character.toString(molecularFormula.charAt(++index));
					while (index < molecularFormula.length() - 1 && Character.isDigit(molecularFormula.charAt(index + 1))) {
						valueString += Character.toString(molecularFormula.charAt(++index));
					}
					count = Integer.parseInt(valueString);

					if (consecutiveElements.isEmpty()) {
						// if elements is now empty we have specified an isotope to apply
						// this
						// amount to
						subMolecule = new ChemicalComposition();
						subMolecule.withIsotope(isotope, count);
					} else {
						// factor out last element and apply amount to it
						Element lastElement = consecutiveElements.remove(consecutiveElements.size() - 1);
						subMolecule = new ChemicalComposition().withElements(consecutiveElements);
						subMolecule.withElement(lastElement, count);
						// add isotope if one was specified for first element
						if (isotope != null) {
							subMolecule.withIsotope(isotope);
						}
					}
				} else {
					// add parsed elements to top of molecule stack.
					subMolecule = new ChemicalComposition().withElements(consecutiveElements);
					// add isotope if one was specified for first element
					if (isotope != null) {
						subMolecule.withIsotope(isotope);
					}
				}
				isotopeSpecifier = null;

				moleculeStack.push(moleculeStack.pop().withMolecule(subMolecule));
			} else if ('(' == currentChar || '{' == currentChar || '[' == currentChar) {
				bracketStack.push(currentChar);
				if (isotopeSpecifier != null) {
					throw new ChemicalFormulaParserException(
							"Unexpected character \"" + currentChar + "\" in \"" + molecularFormula + "\".");
				}
				moleculeStack.push(new ChemicalComposition());
			} else if (')' == currentChar || '}' == currentChar || ']' == currentChar) {
				if (bracketStack.isEmpty()) {
					throw new ChemicalFormulaParserException(
							"Mismatched Brackets (stack underflow) in \"" + molecularFormula + "\".");
				}
				Character opening = bracketStack.pop();
				Character closing = ')';
				switch (opening) {
				case '{':
					closing = '}';
					break;
				case '[':
					closing = ']';
					break;
				}
				if (closing != currentChar) {
					warningConsumer.accept(new ChemicalFormulaParserException(
							"Mismatched Brackets - \"" + opening + " " + currentChar + "\" - in \"" + molecularFormula + "\"."));
				}
				currentMolecule = moleculeStack.pop();

				Integer count = null;
				// find any following consecutive digits
				if (index < molecularFormula.length() - 1 && Character.isDigit(molecularFormula.charAt(index + 1))) {
					String valueString = Character.toString(molecularFormula.charAt(++index));
					while (index < molecularFormula.length() - 1 && Character.isDigit(molecularFormula.charAt(index + 1))) {
						valueString += Character.toString(molecularFormula.charAt(++index));
					}

					count = Integer.parseInt(valueString);
					currentMolecule.withMultipliedCounts(count);
				}

				moleculeStack.push(moleculeStack.pop().withMolecule(currentMolecule));
			} else if (Character.isDigit(currentChar) || '^' == currentChar) {
				if (index + 1 == molecularFormula.length()) {
					throw new ChemicalFormulaParserException(
							"Unexpected character \"" + currentChar + "\" in \"" + molecularFormula + "\".");
				}
				Character nextChar = molecularFormula.charAt(index + 1);
				if ('^' != currentChar || (nextChar != '+' && nextChar != '-')) {
					// a number prefix - to specify isotope
					String valueString;

					if ('^' != currentChar) {
						valueString = Character.toString(currentChar);
					} else {
						valueString = "";
					}
					// find any following consecutive digits
					while (index < molecularFormula.length() - 1
							&& Character.isDigit(nextChar = molecularFormula.charAt(index + 1))) {
						valueString += Character.toString(molecularFormula.charAt(++index));
					}
					if (valueString == "") {
						throw new ChemicalFormulaParserException(
								"Unexpected character \"" + nextChar + "\" in \"" + molecularFormula + "\".");
					}
					isotopeSpecifier = Integer.parseInt(valueString);
				}
			} else if (isotopeSpecifier == null && ((' ' == currentChar || '.' == currentChar || '\t' == currentChar)
					|| (index + 1 != molecularFormula.length() && '-' == currentChar))) {
				if (!alreadyWarned.contains(currentChar)) {
					if (' ' == currentChar) {
						warningConsumer.accept(new ChemicalFormulaParserException("Ignoring extraneous whitespace."));
					} else {
						warningConsumer.accept(new ChemicalFormulaParserException("Ignoring extraneous '" + currentChar + "'."));
					}
					alreadyWarned.add(currentChar);
				}
				// ignore these characters
			} else if (isotopeSpecifier == null && '?' == currentChar) {
				// unknown element! parse proceeding double to get its mass

				if (index == molecularFormula.length() - 1) {
					throw new ChemicalFormulaParserException("Unexpected character \"?\" in \"" + molecularFormula + "\".");
				}

				String valueString = "";
				// find any following consecutive digits
				Character nextChar = ' ';
				while (index < molecularFormula.length() - 1
						&& Character.isDigit(nextChar = molecularFormula.charAt(index + 1))) {
					valueString += Character.toString(molecularFormula.charAt(++index));
				}
				if (molecularFormula.length() >= index + 2 && '.' == (nextChar = molecularFormula.charAt(index + 1))) {
					index++;
					valueString += ".";
					while (index < molecularFormula.length() - 1
							&& Character.isDigit(nextChar = molecularFormula.charAt(index + 1))) {
						valueString += Character.toString(molecularFormula.charAt(++index));
					}
				}
				if (valueString == "") {
					throw new ChemicalFormulaParserException(
							"Unexpected character \"" + nextChar + "\" in \"" + molecularFormula + "\".");
				}
				double mass;
				try {
					mass = Double.parseDouble(valueString);
				} catch (NumberFormatException e) {
					throw new ChemicalFormulaParserException(
							"Unable to parse mass of unknown \"" + valueString + "\" in \"" + molecularFormula + "\".");
				}
				Element unknown = new Element();
				unknown.setName("Unknown element");
				unknown.setSymbol("Uk");
				unknown.setAtomicNumber(0);
				unknown.setCategory(Element.Category.NONE);
				Isotope unknownIsotope = new Isotope(unknown);
				unknownIsotope.setMass(mass);
				unknownIsotope.setAbundance(1);
				unknownIsotope.setMassNumber((int) mass);
				unknown.addIsotope(unknownIsotope);
				moleculeStack.peek().withElement(unknown);
			} else if ('+' == currentChar || '-' == currentChar) {
				if (index + 1 < molecularFormula.length()) {
					// nothing should come after this
					throw new ChemicalFormulaParserException(
							"Unexpected character \"" + molecularFormula.charAt(index + 1) + "\" in \"" + molecularFormula + "\".");
				}

				charge = 1;
				if (isotopeSpecifier != null) {
					charge = isotopeSpecifier.intValue();
				}
				isotopeSpecifier = null;
				if ('-' == currentChar) {
					charge = -charge;
				}
			} else {
				// not a known character
				throw new ChemicalFormulaParserException(
						"Unexpected character \"" + currentChar + "\" in \"" + molecularFormula + "\".");
			}

			index++;
		}

		if (isotopeSpecifier != null) {
			// shouldn't end on a superscript number
			throw new ChemicalFormulaParserException("Unexpected end of line in \"" + molecularFormula + "\".");
		}

		while (moleculeStack.size() > 1) {
			warningConsumer.accept(new ChemicalFormulaParserException(
					"Mismatched Brackets (expecting closing bracket) in \"" + molecularFormula + "\"."));

			currentMolecule = moleculeStack.pop();
			moleculeStack.push(moleculeStack.pop().withMolecule(currentMolecule));
		}

		return moleculeStack.pop().withCharge(charge);
	}

	/**
	 *
	 * @param consecutiveElementString
	 *          a string of elements unbroken by numbers or brackets
	 * @return a list of the most likely elements represented by
	 *         consecutiveElementString based on capitalisation and using sensible
	 *         fall-backs when proper capitalisation gives invalid input
	 */
	private Vector<Element> parseConsecutiveElements(String molecularFormula, String consecutiveElementString,
			Consumer<Exception> warningConsumer) throws ChemicalFormulaParserException {
		/*
		 * for only 1 character long.
		 */
		if (consecutiveElementString.length() == 1) {
			Vector<Element> consecutiveElements = new Vector<Element>();

			Iterator<? extends Element> elementIterator = elements.iterator();
			Element element;
			String symbol;

			while (elementIterator.hasNext()) {
				element = elementIterator.next();
				symbol = element.getSymbol();

				if (symbol.toLowerCase().equals(consecutiveElementString.toLowerCase())) {
					if (Character.isLowerCase(consecutiveElementString.charAt(0))) {
						warningConsumer
								.accept(new ChemicalFormulaParserException("Proceding with invalid capitalisation of symbol \""
										+ element.getSymbol() + "\" in \"" + molecularFormula + "\"."));
					}
					consecutiveElements.add(element);
					return consecutiveElements;
				}
			}

			throw new ChemicalFormulaParserException("Symbols cannot be matched in \"" + molecularFormula + "\".");
		}

		/*
		 * for only 2 characters long.
		 */
		if (consecutiveElementString.length() == 2) {
			Vector<Element> consecutiveElements = new Vector<Element>();

			Iterator<? extends Element> elementIterator = elements.iterator();
			Element element;
			String symbol;

			/*
			 * if capitalised Aa or aa assume user means one single element first...
			 */
			if (Character.isLowerCase(consecutiveElementString.charAt(1))) {
				Element firstElement = null;
				Element secondElement = null;

				while (elementIterator.hasNext()) {
					element = elementIterator.next();
					symbol = element.getSymbol();

					// if string matches a 2 character element symbol then return
					// element as full list
					if (symbol.length() == 2 && symbol.toLowerCase().equals(consecutiveElementString.toLowerCase())) {

						if (Character.isLowerCase(consecutiveElementString.charAt(0))) {
							warningConsumer
									.accept(new ChemicalFormulaParserException("Proceding with invalid capitalisation of symbol \""
											+ element.getSymbol() + "\" in \"" + molecularFormula + "\"."));
						}
						consecutiveElements.add(element);
						return consecutiveElements;
					}

					// if first / second character only matches symbol then keep match
					// for if 2 character symbol match fails.
					if (symbol.length() == 1) {
						if (firstElement == null && symbol.toLowerCase()
								.equals((Character.toString(Character.toLowerCase(consecutiveElementString.charAt(0)))))) {
							firstElement = element;
						}
						if (secondElement == null && symbol.toLowerCase()
								.equals((Character.toString(Character.toLowerCase(consecutiveElementString.charAt(1)))))) {
							secondElement = element;
						}
					}
				}
				// no 2 character symbol match! did we find a pair of 1 character
				// matches?
				if (firstElement == null || secondElement == null)
					throw new ChemicalFormulaParserException("Symbols cannot be matched in \"" + molecularFormula + "\".");

				if (Character.isLowerCase(consecutiveElementString.charAt(0))) {
					warningConsumer
							.accept(new ChemicalFormulaParserException("Proceding with invalid capitalisation for symbol \""
									+ firstElement.getSymbol() + "\" in \"" + molecularFormula + "\"."));
				}
				consecutiveElements.add(firstElement);
				warningConsumer.accept(new ChemicalFormulaParserException("Proceding with invalid capitalisation for symbol \""
						+ secondElement.getSymbol() + "\" in \"" + molecularFormula + "\"."));
				consecutiveElements.add(secondElement);
				return consecutiveElements;

				/*
				 * ...else if capitalised as AA (or aA) assume user means two separate
				 * elements first.
				 */
			} else {
				Element firstElement = null;
				Element secondElement = null;
				Element twoCharacterElement = null;

				while (elementIterator.hasNext()) {
					element = elementIterator.next();
					symbol = element.getSymbol();

					// if first / second character only matches symbol then keep match
					// until found both, then return pair as element list.
					if (symbol.length() == 1) {
						if (firstElement == null && symbol.toLowerCase()
								.equals((Character.toString(Character.toLowerCase(consecutiveElementString.charAt(0)))))) {
							firstElement = element;
						}
						if (secondElement == null && symbol.toLowerCase()
								.equals((Character.toString(Character.toLowerCase(consecutiveElementString.charAt(1)))))) {
							secondElement = element;
						}
						// found both now! return them
						if (firstElement != null && secondElement != null) {
							if (Character.isLowerCase(consecutiveElementString.charAt(0))) {
								warningConsumer
										.accept(new ChemicalFormulaParserException("Proceding with invalid capitalisation for symbol \""
												+ firstElement.getSymbol() + "\" in \"" + molecularFormula + "\"."));
							}
							consecutiveElements.add(firstElement);
							consecutiveElements.add(secondElement);
							return consecutiveElements;
						}
					}

					// if string matches a 2 character element symbol then keep match
					// for if single character element match fails
					if (symbol.length() == 2 && symbol.toLowerCase().equals(consecutiveElementString.toLowerCase())) {
						twoCharacterElement = element;
					}
				}
				// no single character symbol matches! did we find a 2 character one?
				if (twoCharacterElement == null)
					throw new ChemicalFormulaParserException("Symbols cannot be matched in \"" + molecularFormula + "\".");

				warningConsumer.accept(new ChemicalFormulaParserException("Proceding with invalid capitalisation for symbol \""
						+ twoCharacterElement.getSymbol() + "\" in \"" + molecularFormula + "\"."));
				consecutiveElements.add(twoCharacterElement);
				return consecutiveElements;
			}
		}

		/*
		 * for string of greater than 2 characters, find best matching sequence of
		 * 1char and 2char elements (e.g. Hydrogen=H or Argon=Ar respectively) based
		 * on capitalisation
		 *
		 * algorithm needs to backtrack when encountering a fail.
		 *
		 * exhaust possibility by propagating failure back. fail at finding 1char
		 * element AND 2char element means total fail at that index. 2char element
		 * fail 2 characters before a total fail is also a total fail, as is 1char
		 * element fail 1 character before.
		 */

		// consecutive elements found so far
		Vector<Element> consecutiveElements = new Vector<Element>();
		// keep track of invalid capitalisations
		Vector<Boolean> invalidCapitalisation = new Vector<Boolean>();
		// known indices of failure for matching single character elements in
		// string
		Set<Integer> oneCharacterFail = new TreeSet<Integer>();
		// known indices of failure for matching two character elements in string
		Set<Integer> twoCharacterFail = new TreeSet<Integer>();
		// last character pre-fail
		twoCharacterFailAtIndex(molecularFormula, consecutiveElementString.length() - 1, oneCharacterFail,
				twoCharacterFail);

		Iterator<? extends Element> elementIterator;
		Element element;
		String symbol;

		// current character index of algorithm
		int index = 0;
		// found a match in current pass
		boolean found;

		while (index < consecutiveElementString.length()) {
			if (oneCharacterFail.contains(index) && twoCharacterFail.contains(index)) {
				throw new ChemicalFormulaParserException("Symbols cannot be matched in \"" + molecularFormula + "\".");
			}

			elementIterator = elements.iterator();
			found = false;

			/*
			 * if capitalised Aa or aa assume user means one single element first...
			 */
			if ((index < consecutiveElementString.length() - 1) && !twoCharacterFail.contains(index)
					&& (oneCharacterFail.contains(index) || Character.isLowerCase(consecutiveElementString.charAt(index + 1)))) {
				Element oneCharacterElement = null;

				while (!found && elementIterator.hasNext()) {
					element = elementIterator.next();
					symbol = element.getSymbol();

					// if string matches a 2 character element symbol then add
					// element to list
					if (symbol.length() == 2
							&& symbol.toLowerCase().equals(consecutiveElementString.substring(index, index + 2).toLowerCase())) {
						invalidCapitalisation.add(Character.isLowerCase(consecutiveElementString.charAt(index))
								|| Character.isUpperCase(consecutiveElementString.charAt(index + 1)));
						consecutiveElements.add(element);
						index += 2;
						found = true;

						// if one character only matches symbol then keep match
						// for if 2 character symbol match fails.
					} else if (symbol.length() == 1 && oneCharacterElement == null) {
						if (symbol.toLowerCase()
								.equals(Character.toString(Character.toLowerCase(consecutiveElementString.charAt(index))))) {
							oneCharacterElement = element;
						}
					}
				}
				if (!found) {
					// no 2 character symbol match! did we find a 1 character
					// match?
					twoCharacterFailAtIndex(molecularFormula, index, oneCharacterFail, twoCharacterFail);
					if (oneCharacterElement == null) {
						oneCharacterFailAtIndex(molecularFormula, index, oneCharacterFail, twoCharacterFail);
						if (index == 0)
							throw new ChemicalFormulaParserException("Symbols cannot be matched in \"" + molecularFormula + "\".");
						index -= consecutiveElements.lastElement().getSymbol().length();
						invalidCapitalisation.removeElementAt(consecutiveElements.size() - 1);
						consecutiveElements.removeElementAt(consecutiveElements.size() - 1);
					} else {
						invalidCapitalisation.add(Character.isLowerCase(consecutiveElementString.charAt(index)));
						consecutiveElements.add(oneCharacterElement);
						index++;
						found = true;
					}
				}

				/*
				 * ...else if capitalised as AA (or aA) assume user means two separate
				 * elements first.
				 */
			} else {
				Element twoCharacterElement = null;

				while (!found && elementIterator.hasNext()) {
					element = elementIterator.next();
					symbol = element.getSymbol();

					// if one character only matches symbol then match element.
					if (symbol.length() == 1 && symbol.toLowerCase()
							.equals((Character.toString(Character.toLowerCase(consecutiveElementString.charAt(index)))))) {
						invalidCapitalisation.add(Character.isLowerCase(consecutiveElementString.charAt(index)));
						consecutiveElements.add(element);
						index++;
						found = true;

						// if string matches a 2 character element symbol then keep match
						// for if single character element match fails
					} else if (symbol.length() == 2 && (index < consecutiveElementString.length() - 1)
							&& symbol.toLowerCase().equals(consecutiveElementString.substring(index, index + 2).toLowerCase())) {
						twoCharacterElement = element;
					}
				}
				if (!found) {
					// no single character symbol matches! did we find a 2 character
					// one?
					oneCharacterFailAtIndex(molecularFormula, index, oneCharacterFail, twoCharacterFail);
					if (twoCharacterElement == null) {
						if (consecutiveElements.isEmpty()) {
							throw new ChemicalFormulaParserException("Symbols cannot be matched in \"" + molecularFormula + "\".");
						}
						twoCharacterFailAtIndex(molecularFormula, index, oneCharacterFail, twoCharacterFail);
						index -= consecutiveElements.lastElement().getSymbol().length();
						invalidCapitalisation.removeElementAt(consecutiveElements.size() - 1);
						consecutiveElements.removeElementAt(consecutiveElements.size() - 1);
					} else {
						invalidCapitalisation.add(Character.isLowerCase(consecutiveElementString.charAt(index))
								|| Character.isUpperCase(consecutiveElementString.charAt(index + 1)));
						consecutiveElements.add(twoCharacterElement);
						index += 2;
						found = true;
					}
				}
			}
		}

		Iterator<Boolean> invalidCapitalisationIterator = invalidCapitalisation.iterator();
		elementIterator = consecutiveElements.iterator();
		while (invalidCapitalisationIterator.hasNext()) {
			element = elementIterator.next();
			if (invalidCapitalisationIterator.next()) {
				warningConsumer.accept(new ChemicalFormulaParserException("Proceding with invalid capitalisation for symbol \""
						+ element.getSymbol() + "\" in \"" + molecularFormula + "\"."));
			}
		}
		return consecutiveElements;
	}

	// note failure at finding single character symbol at index, and check if
	// parse has become impossible
	private void oneCharacterFailAtIndex(String molecularFormula, int index, Set<Integer> oneCharacterFail,
			Set<Integer> twoCharacterFail) throws ChemicalFormulaParserException {
		if (!oneCharacterFail.contains(index)) {
			oneCharacterFail.add(index);

			if (twoCharacterFail.contains(index)) {
				if (twoCharacterFail.contains(index - 1)) {
					throw new ChemicalFormulaParserException("Symbols cannot be matched in \"" + molecularFormula + "\".");
				}
				if (oneCharacterFail.contains(index + 1) && twoCharacterFail.contains(index + 1)) {
					throw new ChemicalFormulaParserException("Symbols cannot be matched in \"" + molecularFormula + "\".");
				}
				oneCharacterFailAtIndex(molecularFormula, index - 1, oneCharacterFail, twoCharacterFail);
				twoCharacterFailAtIndex(molecularFormula, index - 2, oneCharacterFail, twoCharacterFail);
			}
		}
	}

	// note failure at finding two character symbol at index, and check if
	// parse has become impossible
	private void twoCharacterFailAtIndex(String molecularFormula, int index, Set<Integer> oneCharacterFail,
			Set<Integer> twoCharacterFail) throws ChemicalFormulaParserException {
		if (!twoCharacterFail.contains(index)) {
			twoCharacterFail.add(index);

			if (oneCharacterFail.contains(index)) {
				if (twoCharacterFail.contains(index - 1)) {
					throw new ChemicalFormulaParserException("Symbols cannot be matched in \"" + molecularFormula + "\".");
				}
				if (oneCharacterFail.contains(index + 1) && twoCharacterFail.contains(index + 1)) {
					throw new ChemicalFormulaParserException("Symbols cannot be matched in \"" + molecularFormula + "\".");
				}
				oneCharacterFailAtIndex(molecularFormula, index - 1, oneCharacterFail, twoCharacterFail);
				twoCharacterFailAtIndex(molecularFormula, index - 2, oneCharacterFail, twoCharacterFail);
			}
		}
	}
}
