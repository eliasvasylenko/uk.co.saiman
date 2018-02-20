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
 * This file is part of uk.co.saiman.instrument.stage.copley.
 *
 * uk.co.saiman.instrument.stage.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.copley;

import static java.util.Arrays.asList;
import static uk.co.saiman.instrument.InstrumentLifecycleState.BEGIN_OPERATION;
import static uk.co.saiman.observable.Observable.merge;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import uk.co.saiman.comms.copley.CopleyAxis;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.stage.StageDimension;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.text.properties.PropertyLoader;

public class CopleyXYStageDevice extends CopleyStageDevice implements XYStageDevice {
  private Quantity<Length> homeX;
  private Quantity<Length> homeY;

  private Quantity<Length> exchangeX;
  private Quantity<Length> exchangeY;

  private StageDimension<Length> xAxis;
  private StageDimension<Length> yAxis;

  private boolean requestedExchange;
  private boolean requestedHome;

  protected void initialize(
      Instrument instrument,
      CopleyAxis xAxis,
      CopleyAxis yAxis,
      PropertyLoader loader) {
    super.initialize(instrument, asList(xAxis, yAxis), loader);
    instrument.lifecycleState().filter(BEGIN_OPERATION::equals).observe(o -> moveToHome());
  }

  protected void configure(
      Quantity<Length> lowerBoundX,
      Quantity<Length> lowerBoundY,
      Quantity<Length> upperBoundX,
      Quantity<Length> upperBoundY,
      Quantity<Length> homeX,
      Quantity<Length> homeY,
      Quantity<Length> exchangeX,
      Quantity<Length> exchangeY,
      Units units) {
    this.homeX = homeX;
    this.homeY = homeY;
    this.exchangeX = exchangeX;
    this.exchangeY = exchangeY;

    xAxis = new CopleyLinearDimension(
        units,
        () -> getAxis(0).orElse(null),
        lowerBoundX,
        upperBoundX);
    yAxis = new CopleyLinearDimension(
        units,
        () -> getAxis(1).orElse(null),
        lowerBoundY,
        upperBoundY);

    merge(xAxis.requestedPosition(), yAxis.requestedPosition()).observe(p -> {
      synchronized (CopleyXYStageDevice.this) {
        requestedHome = false;
        requestedExchange = false;
      }
    });
  }

  @Override
  public String getName() {
    return getProperties().copleyXYStageName().get();
  }

  @Override
  public StageDimension<Length> getXAxis() {
    return xAxis;
  }

  @Override
  public StageDimension<Length> getYAxis() {
    return yAxis;
  }

  public synchronized void moveToHome() {
    if (!requestedHome) {
      xAxis.requestedPosition().set(homeX);
      yAxis.requestedPosition().set(homeY);

      requestedExchange = false;
      requestedHome = true;

      // TODO wait for move
    }
  }

  public synchronized void moveToExchange() {
    if (!requestedExchange) {
      moveToHome();
      getInstrument().requestStandby();

      xAxis.requestedPosition().set(exchangeX);
      yAxis.requestedPosition().set(exchangeY);

      requestedExchange = true;
      requestedHome = false;

      // TODO wait for move
    }
  }
}
