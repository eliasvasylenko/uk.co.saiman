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

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.instrument.ControllerStatus;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.DeviceStatus;
import uk.co.saiman.instrument.sample.RequestedSampleState;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.maldi.stage.MaldiStage.MaldiStageConfiguration;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.observable.ObservableValue;

@Designate(ocd = MaldiStageConfiguration.class, factory = true)
@Component(
    configurationPid = MaldiStage.CONFIGURATION_PID,
    configurationPolicy = REQUIRE,
    service = { Device.class, XYStage.class, Stage.class, MaldiStage.class },
    immediate = true)
public class MaldiStage implements XYStage<MaldiStageController> {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Maldi Stage")
  public @interface MaldiStageConfiguration {
    @AttributeDefinition(name = "X Offset")
    String offset() default "(0 m, 0 m)";
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.maldi.stage";

  static final String DEFAULT_OFFSET = "(0 mm, 0 mm)";
  static final String DEFAULT_LOWER_BOUND = "(-27.5 mm, -20 mm)";
  static final String DEFAULT_UPPER_BOUND = "(27.5 mm, 20 mm)";

  private final XYStage<?> stage;
  private final XYCoordinate<Length> offset;
  private final XYCoordinate<Length> lowerBound;
  private final XYCoordinate<Length> upperBound;

  @Activate
  public MaldiStage(
      MaldiStageConfiguration configuration,
      @Reference(name = "stage") XYStage<?> stage) {
    this(
        stage,
        XYCoordinate.fromString(configuration.offset()).asType(Length.class),
        XYCoordinate.fromString(DEFAULT_LOWER_BOUND).asType(Length.class),
        XYCoordinate.fromString(DEFAULT_UPPER_BOUND).asType(Length.class));
  }

  public MaldiStage(
      XYStage<?> stage,
      XYCoordinate<Length> offset,
      XYCoordinate<Length> lowerBound,
      XYCoordinate<Length> upperBound) {
    this.stage = stage;
    this.offset = offset;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @Override
  public ObservableValue<RequestedSampleState<XYCoordinate<Length>>> requestedSampleState() {
    return stage.requestedSampleState();
  }

  @Override
  public ObservableValue<SampleState<XYCoordinate<Length>>> sampleState() {
    return stage.sampleState();
  }

  @Override
  public boolean isPositionReachable(XYCoordinate<Length> location) {
    return stage.isPositionReachable(location.subtract(offset));
  }

  @Override
  public ObservableValue<XYCoordinate<Length>> samplePosition() {
    return stage.samplePosition();
  }

  @Override
  public MaldiStageController acquireControl(long timeout, TimeUnit unit)
      throws TimeoutException, InterruptedException {
    return new MaldiStageController(stage.acquireControl(timeout, unit));
  }

  @Override
  public ObservableValue<ControllerStatus> controllerStatus() {
    return stage.controllerStatus();
  }

  @Override
  public ObservableValue<DeviceStatus> status() {
    return stage.status();
  }

  @Override
  public XYCoordinate<Length> getLowerBound() {
    return lowerBound;
  }

  @Override
  public XYCoordinate<Length> getUpperBound() {
    return upperBound;
  }
}
