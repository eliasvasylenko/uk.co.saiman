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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.instrument.axis.AxisController;
import uk.co.saiman.instrument.axis.AxisDevice;
import uk.co.saiman.instrument.sample.RequestedSampleState;
import uk.co.saiman.instrument.sample.SampleDevice;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.instrument.stage.XYStageController;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

@Designate(ocd = ComposedXYStage.ComposedXYStageConfiguration.class, factory = true)
@Component(
    name = ComposedXYStage.CONFIGURATION_PID,
    configurationPid = ComposedXYStage.CONFIGURATION_PID,
    configurationPolicy = REQUIRE,
    service = { SampleDevice.class, Stage.class, XYStage.class })
public class ComposedXYStage extends ComposedStage<XYCoordinate<Length>, XYStageController>
    implements XYStage {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.composed.xy";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      id = CONFIGURATION_PID,
      name = "Composed XY Stage Configuration",
      description = "The configuration for a modular stage composed of an x axis and a y axis")
  public @interface ComposedXYStageConfiguration {
    String exchangeLocation();

    String analysisLocation();
  }

  private final AxisDevice<Length> xAxis;
  private final AxisDevice<Length> yAxis;

  private AxisController<Length> xController;
  private AxisController<Length> yController;

  @Activate
  public ComposedXYStage(
      @Reference(name = "xAxis") AxisDevice<Length> xAxis,
      @Reference(name = "yAxis") AxisDevice<Length> yAxis,
      ComposedXYStageConfiguration configuration) {
    this(
        xAxis,
        yAxis,
        XYCoordinate.fromString(configuration.analysisLocation()).asType(Length.class),
        XYCoordinate.fromString(configuration.exchangeLocation()).asType(Length.class));
  }

  public ComposedXYStage(
      AxisDevice<Length> xAxis,
      AxisDevice<Length> yAxis,
      XYCoordinate<Length> analysisLocation,
      XYCoordinate<Length> exchangeLocation) {
    super(analysisLocation, exchangeLocation, xAxis, yAxis);

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
      RequestedSampleState<XYCoordinate<Length>> requestedState,
      XYCoordinate<Length> requestedPosition) {
    xController.requestLocation(requestedPosition.getX());
    yController.requestLocation(requestedPosition.getY());
  }

  @Override
  protected void destroyController(ControlContext context) {
    super.destroyController(context);

    try {
      xController.close();
    } finally {
      yController.close();
    }
  }

  @Override
  protected XYStageController createController(ControlContext context)
      throws TimeoutException, InterruptedException {
    try {
      xController = xAxis.acquireControl(2, SECONDS);
      yController = yAxis.acquireControl(2, SECONDS);

      return new XYStageController() {
        @Override
        public void requestExchange() {
          try (var lock = context.acquireLock()) {
            ComposedXYStage.this.requestSampleState(SampleState.exchange());
          }
        }

        @Override
        public void requestAnalysis(XYCoordinate<Length> location) {
          try (var lock = context.acquireLock()) {
            ComposedXYStage.this.requestSampleState(SampleState.analysis(location));
          }
        }

        @Override
        public void requestReady() {
          try (var lock = context.acquireLock()) {
            ComposedXYStage.this.requestSampleState(SampleState.ready());
          }
        }

        @Override
        public SampleState<XYCoordinate<Length>> awaitRequest(long time, TimeUnit unit) {
          try (var lock = context.acquireLock()) {
            return ComposedXYStage.this.awaitRequest(time, unit);
          }
        }

        @Override
        public SampleState<XYCoordinate<Length>> awaitReady(long time, TimeUnit unit) {
          try (var lock = context.acquireLock()) {
            return ComposedXYStage.this.awaitReady(time, unit);
          }
        }

        @Override
        public void close() {
          context.close();
        }

        @Override
        public boolean isOpen() {
          return context.isOpen();
        }
      };
    } catch (Throwable t) {
      if (xController != null) {
        try {
          xController.close();
        } catch (Exception e) {
          t.addSuppressed(e);
        }
      }
      if (yController != null) {
        try {
          yController.close();
        } catch (Exception e) {
          t.addSuppressed(e);
        }
      }
      throw t;
    }
  }
}
