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
 * This file is part of uk.co.saiman.simulation.msapex.
 *
 * uk.co.saiman.simulation.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.msapex.i18n;

import java.util.List;

import uk.co.saiman.properties.Localized;

@SuppressWarnings("javadoc")
public interface SimulationProperties {
  Localized<String> acquisitionSimulationDeviceName();

  Localized<String> sampleImage();

  Localized<String> redChemical();

  Localized<String> greenChemical();

  Localized<String> blueChemical();

  Localized<String> loadSampleImageTitle();

  Localized<String> imageFileFilterTitle();

  List<String> imageFileFilter();

  String invalidAcquisitionCount(int count);
}
