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
 * This file is part of uk.co.saiman.saint.
 *
 * uk.co.saiman.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.saint.stage.impl;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.instrument.DeviceStatus.UNAVAILABLE;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS_READY;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS_READY_REQUESTED;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS_REQUESTED;
import static uk.co.saiman.instrument.sample.SampleState.EXCHANGE_REQUESTED;

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import javax.measure.quantity.Length;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.sample.Analysis;
import uk.co.saiman.instrument.sample.Exchange;
import uk.co.saiman.instrument.sample.Ready;
import uk.co.saiman.instrument.sample.RequestedSampleState;
import uk.co.saiman.instrument.sample.SampleDevice;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.instrument.stage.XYStageController;
import uk.co.saiman.instrument.virtual.DeviceDependency;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.properties.PropertyLoader;
import uk.co.saiman.saint.SaintProperties;
import uk.co.saiman.saint.stage.SampleArea;
import uk.co.saiman.saint.stage.SampleAreaStage;
import uk.co.saiman.saint.stage.SampleAreaStageController;
import uk.co.saiman.saint.stage.SamplePlateStage;

/**
 * An implementation of a stage for the Saint instrument which is backed by an
 * {@link XYStage} implementation. The backing stage must be rectangular, with
 * an accessible area over the Saint sample plate.
 * 
 * @author Elias N Vasylenko
 *
 */
@Designate(ocd = SaintStageManager.SaintStageConfiguration.class, factory = true)
@Component(name = SaintStageManager.CONFIGURATION_PID, configurationPid = SaintStageManager.CONFIGURATION_PID, configurationPolicy = REQUIRE, service = {
    SamplePlateStage.class,
    SampleDevice.class,
    Stage.class,
    Device.class })
public class SaintStageManager {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.saint";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(id = CONFIGURATION_PID, name = "SAINT Stage Configuration", description = "The configuration for a modular stage composed of an x axis and a y axis")
  public @interface SaintStageConfiguration {}

  private final XYStage<?> xyStage;
  private DeviceDependency<? extends XYStageController> xyStageController;

  private final SamplePlateStageImpl samplePlateStage;
  private ServiceRegistration<?> samplePlateRegistration;
  private final SampleAreaStageImpl sampleAreaStage;
  private ServiceRegistration<?> sampleAreaRegistration;

  public SaintStageManager(XYStage<?> xyStage, SaintProperties properties) {
    this.xyStage = xyStage;
    this.xyStageController = deviceDependency(xyStage, true);

    this.samplePlateStage = new SamplePlateStageImpl(this, properties);
    this.sampleAreaStage = new SampleAreaStageImpl(this, properties);
    this.stateMachine = new SaintStageManager(xyStage, xyStageController, this, sampleAreaStage);

    xyStage.sampleState().value().observe(this::updateXySampleState);
  }

  @Activate
  public SaintStageManager(
      BundleContext context,
      @Reference(name = "xyStage") XYStage<?> xyStage,
      SaintStageConfiguration configuration,
      @Reference PropertyLoader propertyLoader) {
    this(xyStage, propertyLoader.getProperties(SaintProperties.class));

    /*
     * TODO need a new deviceDependency type, "non-acquiring", so both devices can
     * be dependent upon the xyStage, even though it's the manager which acquires
     * the xyController as a third party, on their behalf. change current boolean
     * parameter to accept an enum...
     */

    this.sampleAreaRegistration = context
        .registerService(
            new String[] {
                SampleAreaStage.class.getName(),
                Stage.class.getName(),
                SampleDevice.class.getName(),
                Device.class.getName() },
            this.sampleAreaStage,
            new Hashtable<>());
    this.samplePlateRegistration = context
        .registerService(
            new String[] {
                SamplePlateStage.class.getName(),
                Stage.class.getName(),
                SampleDevice.class.getName(),
                Device.class.getName() },
            this.samplePlateStage,
            new Hashtable<>());
  }

  @Deactivate
  public void deactivate() {
    if (sampleAreaRegistration != null) {
      sampleAreaRegistration.unregister();
      sampleAreaRegistration = null;
    }
    if (samplePlateRegistration != null) {
      samplePlateRegistration.unregister();
      samplePlateRegistration = null;
    }
  }

  public XYStage<?> xyStage() {
    return xyStage;
  }

  public SamplePlateStageImpl samplePlateStage() {
    return samplePlateStage;
  }

  public SampleAreaStageImpl sampleAreaStage() {
    return sampleAreaStage;
  }

  public Instrument getInstrument() {
    return xyStage.getInstrumentRegistration().getInstrument();
  }

  synchronized void processState() {
    if (isAtAnalysis()) {
      /*
       * Our sample area device is acquired and ready for analysis, so it is given
       * command of the XY stage controller.
       */

      if (sampleAreaStage.requestedSampleState().get() instanceof Ready<?>) {

      } else if (sampleAreaStage.requestedSampleState().get() instanceof Analysis<?>) {
        var requestedSampleState = (Analysis<SampleArea>) samplePlateStage
            .requestedSampleState()
            .get();
        var sampleAreaRequestedSampleState = (Analysis<XYCoordinate<Length>>) sampleAreaStage
            .requestedSampleState()
            .get();
        var analysisPosition = requestedSampleState
            .position()
            .center()
            .add(sampleAreaRequestedSampleState.position());

        if (requestXyState(SampleState.analysis(analysisPosition))) {
          this.sampleAreaStage.requestReached();
        } else if (requestXyStateFailed()) {
          this.sampleAreaStage.requestFailed();
        }
      }
    } else {
      /*
       * Our sample area device is released or ready for exchange, so our sample plate
       * device is given command of the XY stage controller.
       */

      if (requestedSampleState().get() instanceof Analysis<?>) {
        requestXyPosition(requestedLocation.get().center());
        if (xyStageState == ANALYSIS) {
          this.sampleState.set(ANALYSIS);
        } else if (xyStageState != ANALYSIS_REQUESTED) {
          this.sampleState.set(FAILED);
        }

      } else if (requestedSampleState().get().equals(SampleState.exchange())) {
        getController(this.xyStageController).requestExchange();
        if (xyStage.sampleState().isMatching(s -> s instanceof Exchange<?>)) {
          this.sampleState.set(EXCHANGE);
        } else if (xyStageState != EXCHANGE_REQUESTED) {
          this.sampleState.set(FAILED);
        }

      } else if (requestedSampleState().get().equals(SampleState.ready())) {
        getController(this.xyStageController).requestReady();
        if (xyStageState == ANALYSIS_READY || xyStageState == ANALYSIS) {
          this.sampleState.set(ANALYSIS_READY);
        } else if (xyStageState != ANALYSIS_READY_REQUESTED) {
          this.sampleState.set(FAILED);
        }
      }
    }
  }

  private boolean requestXyState(RequestedSampleState<XYCoordinate<Length>> state) {
    if (!xyStage.requestedSampleState().isEqual(state)) {
      try (var control = xyStage.acquireControl(500, TimeUnit.MILLISECONDS)) {
        control.request(state);
      }
    }
    return xyStage.sampleState().isEqual(state);
  }

  private boolean requestXyStateFailed() {
    return xyStage.sampleState().isEqual(SampleState.failed());
  }

  private boolean isAtAnalysis() {
    return (samplePlateStage.sampleState().isMatching(s -> s instanceof Analysis<?>)
        && (samplePlateStage.requestedSampleState().isMatching(s -> s instanceof Analysis<?>)
            || (sampleAreaStage.status().isEqual(UNAVAILABLE)
                && !sampleAreaStage.sampleState().isMatching(r -> r instanceof Exchange<?>))));
  }
}
