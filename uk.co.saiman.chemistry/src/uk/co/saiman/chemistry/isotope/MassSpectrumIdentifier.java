/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.chemistry.
 *
 * uk.co.saiman.chemistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.chemistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.chemistry.isotope;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import uk.co.saiman.chemistry.ChemicalComposition;
import uk.co.saiman.mathematics.Interval;

public class MassSpectrumIdentifier {
	private final Vector<MoleculeCompositionConstraint> constraints;

	public MassSpectrumIdentifier() {
		constraints = new Vector<>();
	}

	public void clearConstraints() {
		constraints.clear();
	}

	public void setConstraint(MoleculeCompositionConstraint constraint) {
		clearConstraints();
		addConstraint(constraint);
	}

	public void addConstraint(MoleculeCompositionConstraint constraint) {
		constraints.add(constraint);
	}

	public Vector<MoleculeCompositionConstraint> getConstraints() {
		return constraints;
	}

	/**
	 * identify an isotope distribution as belonging to a molecule or a set of
	 * molecules.
	 *
	 * @param spectrum
	 *          the mass spectrum to identify
	 * @return a map of results to the algorithms degree of belief of those
	 *         results. each result is in the form of a set of isotope
	 *         distributions which are properly qualified with their associated
	 *         molecules.
	 */
	public HashMap<HashSet<IsotopeDistribution>, Double> identify(IdealMassSpectrum spectrum) {
		// preliminary ungrouped results for each constraint
		HashSet<HashMap<IsotopeDistribution, Double>> ungroupedResults = new HashSet<>();
		HashMap<IsotopeDistribution, Double> constraintResult;

		// go through each constraint and find best matches within them
		Iterator<MoleculeCompositionConstraint> constraintIterator = constraints.iterator();
		MoleculeCompositionConstraint constraint;
		while (constraintIterator.hasNext()) {
			constraint = constraintIterator.next();

			constraintResult = new HashMap<>();

			// go through each possible molecule from this constraint

			Iterator<ChemicalComposition> moleculeIterator = constraint.getConformingMolecules().iterator();
			IsotopeDistribution comparisonDistribution;

			while (moleculeIterator.hasNext()) {
				ChemicalComposition molecule = moleculeIterator.next();

				comparisonDistribution = new IsotopeDistribution();
				double distance = 0;
				try {
					comparisonDistribution.calculateForMolecule(molecule, 500, spectrum.getEffectiveResolution());
					distance = getSimilarityDistance(spectrum, comparisonDistribution);
				} catch (Exception e) {
					distance = 0;
				}

				constraintResult.put(comparisonDistribution, distance);
			}

			ungroupedResults.add(constraintResult);
		}

		/*
		 * we now have a set of lists of best matches along with their similarity
		 * distances. find the best combinations of the matches.
		 */

		// tree map to return
		HashMap<HashSet<IsotopeDistribution>, Double> result = new HashMap<>();

		// TODO make this do it properly...
		Iterator<Map.Entry<IsotopeDistribution, Double>> constraintResultIterator = ungroupedResults.iterator().next()
				.entrySet().iterator();
		while (constraintResultIterator.hasNext()) {
			Map.Entry<IsotopeDistribution, Double> entry = constraintResultIterator.next();

			// a result suggesting a distribution combination
			HashSet<IsotopeDistribution> combinationSet = new HashSet<>();
			if (entry != null && entry.getKey() != null && entry.getValue() != null) {
				combinationSet.add(entry.getKey());
				result.put(combinationSet, entry.getValue());
			}
		}

		return result;
	}

	public HashMap<HashSet<IsotopeDistribution>, Double> identify(IsotopeDistribution distribution) {
		return identify(distribution.getMassSpectrum());
	}

	public static double getSimilarityDistance(IdealMassSpectrum spectrum, IdealMassSpectrum comparisonSpectrum) {
		// work using worse of our effective resolutions
		double effectiveResolution = spectrum.getEffectiveResolution();
		if (comparisonSpectrum.getEffectiveResolution() > effectiveResolution) {
			effectiveResolution = comparisonSpectrum.getEffectiveResolution();
		}

		// get spectrum scaling factor for best fit and normalising constants
		double scalingFactor = spectrum.getNormalisingConstant() * comparisonSpectrum.getNormalisingConstant();

		// range of intersection
		Interval<Double> intersectionRange = spectrum.getRange().getIntersectionWith(comparisonSpectrum.getRange());

		double similaritySum = 0;
		if (!intersectionRange.isEmpty()) {
			double mass;
			double abundance;
			double otherAbundance;
			if (spectrum.getMassStepSize() < comparisonSpectrum.getMassStepSize()) {
				Iterator<Double> intersectionIterator = spectrum.dataFromRange(intersectionRange).keySet().iterator();

				while (intersectionIterator.hasNext()) {
					mass = intersectionIterator.next();
					abundance = spectrum.getData().get(mass);
					otherAbundance = comparisonSpectrum.getInterpolatedAbundance(mass);
					similaritySum += Math.sqrt(otherAbundance * abundance * scalingFactor);
				}
			} else {
				Iterator<Double> intersectionIterator = comparisonSpectrum.dataFromRange(intersectionRange).keySet().iterator();

				while (intersectionIterator.hasNext()) {
					mass = intersectionIterator.next();
					abundance = spectrum.getInterpolatedAbundance(mass);
					otherAbundance = comparisonSpectrum.getData().get(mass);
					similaritySum += Math.sqrt(otherAbundance * abundance * scalingFactor);
				}
			}
		}

		return -Math.log(similaritySum);
	}

	public static double getSimilarityDistance(IsotopeDistribution distribution, IdealMassSpectrum comparisonSpectrum) {
		return getSimilarityDistance(distribution.getMassSpectrum(), comparisonSpectrum);
	}

	public static double getSimilarityDistance(IdealMassSpectrum spectrum, IsotopeDistribution comparisonDistribution) {
		return getSimilarityDistance(spectrum, comparisonDistribution.getMassSpectrum());
	}

	public static double getSimilarityDistance(IsotopeDistribution distribution,
			IsotopeDistribution comparisonDistribution) {
		return getSimilarityDistance(distribution.getMassSpectrum(), comparisonDistribution.getMassSpectrum());
	}
}
