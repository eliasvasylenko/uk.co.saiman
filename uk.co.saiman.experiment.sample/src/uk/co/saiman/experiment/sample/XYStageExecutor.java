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
 * This file is part of uk.co.saiman.experiment.sample.
 *
 * uk.co.saiman.experiment.sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.sample;

import static uk.co.saiman.measurement.Quantities.quantityFormat;
import static uk.co.saiman.state.Accessor.mapAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import uk.co.saiman.experiment.environment.Provision;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.instrument.stage.XYStageController;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;

/**
 * An {@link SampleExecutor experiment type} for {@link XYStage XY stage
 * devices}.
 * 
 * @author Elias N Vasylenko
 */
public interface XYStageExecutor extends StageExecutor<XYCoordinate<Length>> {
  MapIndex<Quantity<Length>> X = lengthAccessor("xOffset");
  MapIndex<Quantity<Length>> Y = lengthAccessor("yOffset");
  Variable<XYCoordinate<Length>> LOCATION = new Variable<>(
      "xySampleLocation",
      mapAccessor(
          s -> new XYCoordinate<>(s.get(X), s.get(Y)),
          l -> StateMap.empty().with(X, l.getX()).with(Y, l.getY())));

  private static MapIndex<Quantity<Length>> lengthAccessor(String value) {
    return new MapIndex<>(
        value,
        stringAccessor()
            .map(l -> quantityFormat().parse(l).asType(Length.class), quantityFormat()::format));
  }

  @Override
  Provision<? extends XYStageController> sampleDevice();

  @Override
  default Variable<XYCoordinate<Length>> sampleLocation() {
    return LOCATION;
  }
}
