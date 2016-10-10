/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
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
package uk.co.saiman.chemistry;

import org.osgi.service.component.annotations.Component;

@Component
public interface ChemicalMeasurement {
	ChemicalComposition getTheoreticalComposition();

	double getMeasuredMass();

	default double getTheoreticalMass() {
		return isMeasurementMonoisotopic() ? getTheoreticalComposition().getMonoisotopicMass()
				: getTheoreticalComposition().getAverageMass();
	}

	double getMeasuredIntensity();

	boolean isMeasurementMonoisotopic();

	default double getError() {
		return getMeasuredMass() - getTheoreticalMass();
	}

	default double getRelativeError() {
		return getError() / getTheoreticalMass();
	}

	static ChemicalMeasurement unknownComposition(double mass, boolean monoisotopic) {
		return unknownComposition(mass, 1, monoisotopic);
	}

	static ChemicalMeasurement unknownComposition(double mass, double intensity, boolean monoisotopic) {
		ChemicalComposition composition = new ChemicalComposition().withElement(new Element(mass));

		return new ChemicalMeasurement() {
			@Override
			public ChemicalComposition getTheoreticalComposition() {
				return composition;
			}

			@Override
			public double getMeasuredMass() {
				return mass;
			}

			@Override
			public double getMeasuredIntensity() {
				return intensity;
			}

			@Override
			public boolean isMeasurementMonoisotopic() {
				return monoisotopic;
			}
		};
	}
}
