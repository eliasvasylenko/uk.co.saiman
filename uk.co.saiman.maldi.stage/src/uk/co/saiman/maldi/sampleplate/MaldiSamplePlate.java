/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.maldi.stage.
 *
 * uk.co.saiman.maldi.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.sampleplate;

import java.util.Optional;

import javax.measure.quantity.Length;

import uk.co.saiman.experiment.sampleplate.SamplePlate;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.state.StateMap;

public interface MaldiSamplePlate extends SamplePlate {
  static final XYCoordinate<Length> LOWER_BOUND = new XYCoordinate<>(
      Units.metre().milli().getUnit(),
      -27.5,
      -20);
  static final XYCoordinate<Length> UPPER_BOUND = new XYCoordinate<>(
      Units.metre().milli().getUnit(),
      27.5,
      20);

  Optional<XYCoordinate<Length>> barcodeLocation();

  @Override
  MaldiSampleArea sampleArea(StateMap state);

  @Override
  default XYCoordinate<Length> lowerBound() {
    return LOWER_BOUND;
  }

  @Override
  default XYCoordinate<Length> upperBound() {
    return UPPER_BOUND;
  }
}
