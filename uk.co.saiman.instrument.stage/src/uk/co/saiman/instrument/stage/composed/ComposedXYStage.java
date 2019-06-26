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
 * This file is part of uk.co.saiman.instrument.stage.
 *
 * uk.co.saiman.instrument.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.composed;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.util.concurrent.TimeUnit;

import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.axis.AxisDevice;
import uk.co.saiman.instrument.sample.RequestedSampleState;
import uk.co.saiman.instrument.sample.SampleDevice;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.instrument.stage.XYStageController;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

@Designate(ocd = ComposedXYStage.ComposedXYStageConfiguration.class, factory = true)
@Component(name = ComposedXYStage.CONFIGURATION_PID, configurationPid = ComposedXYStage.CONFIGURATION_PID, configurationPolicy = REQUIRE, service = {
    SampleDevice.class,
    Stage.class,
    XYStage.class })
public class ComposedXYStage extends ComposedStage<XYCoordinate<Length>, XYStageController>
    implements XYStage<XYStageController> {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.composed.xy";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(id = CONFIGURATION_PID, name = "SAINT Stage Configuration", description = "The configuration for a modular stage composed of an x axis and a y axis")
  public @interface ComposedXYStageConfiguration {
    String name();

    String exchangeLocation();

    String analysisLocation();
  }

  private final AxisDevice<Length, ?> xAxis;
  private final AxisDevice<Length, ?> yAxis;

  @Activate
  public ComposedXYStage(
      @Reference(name = "instrument") Instrument instrument,
      @Reference(name = "xAxis") AxisDevice<Length, ?> xAxis,
      @Reference(name = "yAxis") AxisDevice<Length, ?> yAxis,
      ComposedXYStageConfiguration configuration) {
    this(
        configuration.name(),
        instrument,
        xAxis,
        yAxis,
        XYCoordinate.fromString(configuration.analysisLocation()).asType(Length.class),
        XYCoordinate.fromString(configuration.exchangeLocation()).asType(Length.class));
  }

  public ComposedXYStage(
      String name,
      Instrument instrument,
      AxisDevice<Length, ?> xAxis,
      AxisDevice<Length, ?> yAxis,
      XYCoordinate<Length> analysisLocation,
      XYCoordinate<Length> exchangeLocation) {
    super(name, instrument, analysisLocation, exchangeLocation, xAxis, yAxis);

    this.xAxis = xAxis;
    this.yAxis = yAxis;
  }

  @Override
  public XYCoordinate<Length> getLowerBound() {
    return new XYCoordinate<>(xAxis.getLowerBound(), yAxis.getLowerBound());
  }

  @Override
  public XYCoordinate<Length> getUpperBound() {
    return new XYCoordinate<>(xAxis.getUpperBound(), yAxis.getUpperBound());
  }

  @Override
  public boolean isPositionReachable(XYCoordinate<Length> location) {
    return (getLowerBound().getX().subtract(location.getX()).getValue().doubleValue() <= 0)
        && (getLowerBound().getY().subtract(location.getY()).getValue().doubleValue() <= 0)
        && (getUpperBound().getX().subtract(location.getX()).getValue().doubleValue() >= 0)
        && (getUpperBound().getY().subtract(location.getY()).getValue().doubleValue() >= 0);
  }

  @Override
  protected XYCoordinate<Length> getActualPositionImpl() {
    return new XYCoordinate<>(xAxis.actualPosition().get(), yAxis.actualPosition().get());
  }

  @Override
  protected void setRequestedStateImpl(
      ControlContext context,
      RequestedSampleState<XYCoordinate<Length>> requestedState,
      XYCoordinate<Length> requestedPosition) {
    context.getController(xAxis).requestLocation(requestedPosition.getX());
    context.getController(yAxis).requestLocation(requestedPosition.getY());
  }

  @Override
  protected XYStageController createDependentController(ControlContext context) {
    return new XYStageController() {
      @Override
      public void requestExchange() {
        ComposedXYStage.this.requestSampleState(context, SampleState.exchange());
      }

      @Override
      public void requestAnalysis(XYCoordinate<Length> location) {
        ComposedXYStage.this.requestSampleState(context, SampleState.analysis(location));
      }

      @Override
      public void requestReady() {
        ComposedXYStage.this.requestSampleState(context, SampleState.ready());
      }

      @Override
      public SampleState<XYCoordinate<Length>> awaitRequest(long time, TimeUnit unit) {
        return context.get(() -> ComposedXYStage.this.awaitRequest(time, unit));
      }

      @Override
      public SampleState<XYCoordinate<Length>> awaitReady(long time, TimeUnit unit) {
        return context.get(() -> ComposedXYStage.this.awaitReady(time, unit));
      }
    };
  }
}
