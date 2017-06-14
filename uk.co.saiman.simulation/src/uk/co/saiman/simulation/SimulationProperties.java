/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation;

import java.util.List;
import java.util.Set;

import uk.co.saiman.SaiProperties;
import uk.co.saiman.acquisition.AcquisitionProperties;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.simulation.instrument.DetectorSimulation;
import uk.co.strangeskies.text.properties.Localized;
import uk.co.strangeskies.text.properties.Nested;
import uk.co.strangeskies.text.properties.PropertyConfiguration;
import uk.co.strangeskies.text.properties.PropertyConfiguration.KeyCase;

@SuppressWarnings("javadoc")
@PropertyConfiguration(keyCase = KeyCase.LOWER, keySplitString = ".")
public interface SimulationProperties {
	Localized<String> xyRasterStageSimulationDeviceName();

	Localized<String> acquisitionSimulationDeviceName();

	@Nested
	AcquisitionProperties acquisition();

	@Nested
	ExperimentProperties experiment();

	@Nested
	SaiProperties sai();

	Localized<String> sampleImage();

	Localized<String> redChemical();

	Localized<String> greenChemical();

	Localized<String> blueChemical();

	Localized<String> loadSampleImageTitle();

	Localized<String> imageFileFilterTitle();

	List<String> imageFileFilter();

	String invalidAcquisitionCount(int count);

	String cannotFindDetector(String detectorName, Set<DetectorSimulation> availableDetectors);
}
