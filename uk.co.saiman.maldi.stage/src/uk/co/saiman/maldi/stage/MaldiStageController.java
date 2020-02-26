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
package uk.co.saiman.maldi.stage;

import java.util.concurrent.TimeUnit;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.XYStageController;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class MaldiStageController implements XYStageController {
  private final XYStageController controller;

  public MaldiStageController(XYStageController controller) {
    this.controller = controller;
  }

  @Override
  public void requestExchange() {
    controller.requestExchange();
  }

  @Override
  public void requestReady() {
    controller.requestReady();
  }

  @Override
  public void requestAnalysis(XYCoordinate<Length> position) {
    controller.requestAnalysis(position);
  }

  @Override
  public SampleState<XYCoordinate<Length>> awaitRequest(long timeout, TimeUnit unit) {
    return controller.awaitRequest(timeout, unit);
  }

  @Override
  public SampleState<XYCoordinate<Length>> awaitReady(long timeout, TimeUnit unit) {
    return controller.awaitReady(timeout, unit);
  }

  @Override
  public void close() {
    controller.close();
  }

  @Override
  public boolean isOpen() {
    return controller.isOpen();
  }
}