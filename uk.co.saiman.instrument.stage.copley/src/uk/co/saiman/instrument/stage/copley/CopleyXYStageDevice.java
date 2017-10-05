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

import static uk.co.saiman.instrument.InstrumentLifecycleState.BEGIN_OPERATION;
import static uk.co.saiman.observable.Observable.merge;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.stage.StageDimension;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.text.properties.PropertyLoader;

public class CopleyXYStageDevice extends CopleyStageDevice implements XYStageDevice {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Copley XY Stage Configuration",
      description = "An implementation of an XY stage device interface based on copley motor hardware")
  public @interface CopleyXYStageConfiguration {
    @AttributeDefinition(
        name = "Copley Comms",
        description = "The OSGi reference filter for the comms interface")
    String comms_target() default "(objectClass=uk.co.saiman.comms.copley.CopleyComms)";

    String lowerBoundX();

    String lowerBoundY();

    String upperBoundX();

    String upperBoundY();

    String homePositionX();

    String homePositionY();

    String exchangePositionX();

    String exchangePositionY();
  }

  private Quantity<Length> lowerBoundX;
  private Quantity<Length> lowerBoundY;

  private Quantity<Length> upperBoundX;
  private Quantity<Length> upperBoundY;

  private Quantity<Length> homeX;
  private Quantity<Length> homeY;

  private Quantity<Length> exchangeX;
  private Quantity<Length> exchangeY;

  private StageDimension<Length> xAxis;
  private StageDimension<Length> yAxis;

  private boolean atExchange;
  private boolean atHome;

  @Override
  void initialize(Instrument instrument, CopleyComms comms, PropertyLoader loader) {
    super.initialize(instrument, comms, loader);
    instrument.lifecycleState().filter(BEGIN_OPERATION::equals).observe(o -> moveToHome());
  }

  void configure(CopleyXYStageConfiguration configuration, Units units) {
    lowerBoundX = units.parseQuantity(configuration.lowerBoundX()).asType(Length.class);
    lowerBoundY = units.parseQuantity(configuration.lowerBoundY()).asType(Length.class);

    upperBoundX = units.parseQuantity(configuration.upperBoundX()).asType(Length.class);
    upperBoundY = units.parseQuantity(configuration.upperBoundY()).asType(Length.class);

    homeX = units.parseQuantity(configuration.homePositionX()).asType(Length.class);
    homeY = units.parseQuantity(configuration.homePositionY()).asType(Length.class);

    exchangeX = units.parseQuantity(configuration.exchangePositionX()).asType(Length.class);
    exchangeY = units.parseQuantity(configuration.exchangePositionY()).asType(Length.class);

    xAxis = new CopleyLinearDimension(
        0,
        units,
        () -> getController().orElse(null),
        lowerBoundX,
        upperBoundX);
    yAxis = new CopleyLinearDimension(
        1,
        units,
        () -> getController().orElse(null),
        lowerBoundY,
        upperBoundY);

    merge(xAxis.requestedPosition(), yAxis.requestedPosition()).observe(p -> {
      synchronized (CopleyXYStageDevice.this) {
        atHome = false;
        atExchange = false;
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
    if (!atHome) {
      System.out.println("moving home");

      xAxis.requestedPosition().set(homeX);
      yAxis.requestedPosition().set(homeY);

      atExchange = false;
      atHome = true;

      // TODO wait for move
    }
  }

  public synchronized void moveToExchange() {
    if (!atExchange) {
      moveToHome();
      getInstrument().requestStandby();

      System.out.println("exhanging...");

      xAxis.requestedPosition().set(exchangeX);
      yAxis.requestedPosition().set(exchangeY);

      atExchange = true;
      atHome = false;

      // TODO wait for move
    }
  }
}
