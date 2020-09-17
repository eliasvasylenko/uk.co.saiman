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
 * This file is part of uk.co.saiman.instrument.stage.msapex.
 *
 * uk.co.saiman.instrument.stage.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.msapex;

import java.util.Optional;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.sample.Analysis;
import uk.co.saiman.instrument.sample.RequestedSampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class RequestedXYPositionAnnotation extends XYPositionAnnotation {
  public RequestedXYPositionAnnotation(Stage<XYCoordinate<Length>> stage) {
    stage
        .requestedSampleState()
        .optionalValue()
        .weakReference(this)
        .observe(
            o -> o
                .message()
                .ifPresentOrElse(o.owner()::setRequestedState, o.owner()::unsetRequestedState));
  }

  protected void setRequestedState(
      Optional<RequestedSampleState<XYCoordinate<Length>>> requestedState) {
    requestedState
        .filter(r -> r instanceof Analysis<?>)
        .map(r -> (Analysis<XYCoordinate<Length>>) r)
        .map(Analysis::position)
        .ifPresentOrElse(position -> {
          setRequestedPosition(position);
          setVisible(true);
        }, () -> unsetRequestedState());
  }

  protected void unsetRequestedState() {
    setVisible(false);
  }

  protected void setRequestedPosition(XYCoordinate<Length> position) {
    setMeasurementX(position.getX());
    setMeasurementY(position.getY());
  }
}
