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

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.comms.copley.CopleyVariableID.DRIVE_EVENT_STATUS;
import static uk.co.saiman.instrument.stage.composed.AxisState.DISCONNECTED;
import static uk.co.saiman.instrument.stage.composed.AxisState.LOCATION_REACHED;
import static uk.co.saiman.instrument.stage.composed.AxisState.LOCATION_REQUESTED;
import static uk.co.saiman.measurement.Units.metre;
import static uk.co.saiman.observable.Observable.fixedRate;
import static uk.co.saiman.observable.Observer.onFailure;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.copley.CopleyAxis;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.CopleyException;
import uk.co.saiman.comms.copley.EventStatusRegister;
import uk.co.saiman.comms.copley.Int32;
import uk.co.saiman.instrument.sample.SampleLocationUnknown;
import uk.co.saiman.instrument.stage.composed.AxisState;
import uk.co.saiman.instrument.stage.composed.StageAxis;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.measurement.scalar.Scalar;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.observable.OwnedMessage;

@Designate(ocd = CopleyLinearAxis.CopleyLinearAxisConfiguration.class, factory = true)
@Component(name = CopleyLinearAxis.CONFIGURATION_PID, configurationPid = CopleyLinearAxis.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class CopleyLinearAxis implements StageAxis<Length> {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.copley.linear";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(id = CONFIGURATION_PID, name = "Copley Stage Linear Axis Configuration", description = "The configuration for a linear Copley motor axis for a modular stage")
  public @interface CopleyLinearAxisConfiguration {
    @AttributeDefinition(name = "Controller comms target", description = "The copley controller instance owning the axis")
    String comms_target();

    int axis()

    default 0;

    int node() default 0;
  }

  private final CopleyController comms;
  private final int node;
  private final int axis;

  private final ObservableProperty<Quantity<Length>> actualLocation;

  private final ObservableProperty<AxisState> axisState;

  private final Log log;
  private Disposable polling;

  @Activate
  public CopleyLinearAxis(
      @Reference(name = "comms") CopleyController comms,
      CopleyLinearAxisConfiguration configuration,
      @Reference Log log) {
    this(comms, configuration.node(), configuration.axis(), log);
  }

  public CopleyLinearAxis(CopleyController comms, int node, int axis, Log log) {
    this.log = log;

    this.comms = comms;
    this.node = node;
    this.axis = axis;

    this.actualLocation = new ObservablePropertyImpl<>(new SampleLocationUnknown());
    this.axisState = new ObservablePropertyImpl<>(DISCONNECTED);

    startPolling();
  }

  protected synchronized void startPolling() {
    poll();
    polling = fixedRate(0, 100, MILLISECONDS)
        .weakReference(this)
        .map(OwnedMessage::owner)
        .then(onFailure(t -> log.log(Level.ERROR, t)))
        .observe(CopleyLinearAxis::poll);
  }

  @Deactivate
  protected synchronized void stopPolling() {
    polling.cancel();
  }

  @Override
  public synchronized void requestLocation(Quantity<Length> location) {
    if (axisState.isEqual(LOCATION_REQUESTED)) {
      throw new IllegalStateException("location already requested");
    }

    axisState.set(LOCATION_REQUESTED);

    try {
      var position = new Int32(getStepsFromLength(location));
      getAxis().requestedPosition().set(position);
    } catch (Exception e) {
      axisState.set(DISCONNECTED);
      log.log(Level.ERROR, "Failed to request axis position", e);
      throw e;
    }
  }

  private CopleyAxis getAxis() {
    return comms
        .getAxis(this.node, this.axis)
        .orElseThrow(
            () -> new IllegalStateException(
                format("Cannot find axis %s at node %s", this.axis, this.node)));
  }

  @Override
  public void abortRequest() {
    // TODO Auto-generated method stub

  }

  synchronized void poll() {
    updateActualLocation();
    updateStatus();
  }

  void updateStatus() {
    try {
      var status = (EventStatusRegister) getAxis().variable(DRIVE_EVENT_STATUS).get();
      if (status.driveFault) {
        throw new CopleyException("Drive fault");
      }
      if (!status.motionActive && axisState.isEqual(LOCATION_REQUESTED)) {
        axisState.set(LOCATION_REACHED);
      }
    } catch (Exception e) {
      axisState.set(DISCONNECTED);
      actualLocation.setProblem(new SampleLocationUnknown(e));
      log.log(Level.ERROR, "Failed to determine axis position", e);
    }
  }

  void updateActualLocation() {
    try {
      var position = getAxis().actualPosition().get();
      actualLocation.set(getLengthFromSteps(position.value));
    } catch (Exception e) {
      axisState.set(DISCONNECTED);
      actualLocation.setProblem(new SampleLocationUnknown(e));
      log.log(Level.ERROR, "Failed to determine axis position", e);
    }
  }

  public int getStepsFromLength(Quantity<Length> length) {
    return length.to(metre().micro().getUnit()).getValue().intValue();
  }

  public Quantity<Length> getLengthFromSteps(int steps) {
    return new Scalar<>(metre().micro(), steps);
  }

  @Override
  public ObservableValue<Quantity<Length>> actualLocation() {
    return actualLocation;
  }

  @Override
  public ObservableValue<AxisState> axisState() {
    return axisState;
  }
}
