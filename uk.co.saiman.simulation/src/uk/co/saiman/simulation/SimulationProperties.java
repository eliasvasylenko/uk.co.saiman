/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import uk.co.saiman.acquisition.AcquisitionProperties;
import uk.co.saiman.properties.LocalizedString;
import uk.co.saiman.properties.Nested;
import uk.co.saiman.properties.SaiProperties;

@SuppressWarnings("javadoc")
public interface SimulationProperties {
  LocalizedString acquisitionSimulationDeviceName();

  @Nested
  AcquisitionProperties acquisition();

  @Nested
  SaiProperties sai();

  LocalizedString sampleImage();

  LocalizedString redChemical();

  LocalizedString greenChemical();

  LocalizedString blueChemical();

  LocalizedString loadSampleImageTitle();

  LocalizedString imageFileFilterTitle();

  List<String> imageFileFilter();

  String invalidAcquisitionCount(int count);
}
