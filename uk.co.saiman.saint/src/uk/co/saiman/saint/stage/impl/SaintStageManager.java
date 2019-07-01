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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.instrument.DeviceStatus.UNAVAILABLE;

import java.util.Hashtable;
import java.util.function.Function;

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
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.properties.PropertyLoader;
import uk.co.saiman.saint.SaintProperties;
import uk.co.saiman.saint.stage.SampleArea;
import uk.co.saiman.saint.stage.SampleAreaStage;
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
@Component(name = SaintStageManager.CONFIGURATION_PID, configurationPid = SaintStageManager.CONFIGURATION_PID, configurationPolicy = REQUIRE, service = SaintStageManager.class, immediate = true)
public class SaintStageManager {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.saint";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(id = CONFIGURATION_PID, name = "SAINT Stage Configuration", description = "The configuration for a modular stage composed of an x axis and a y axis")
  public @interface SaintStageConfiguration {}

  private final XYStage<?> xyStage;
  private DeviceDependency<? extends XYStageController> xyStageController;
  private Disposable observer;

  private final SamplePlateStageImpl samplePlateStage;
  private ServiceRegistration<?> samplePlateRegistration;
  private final SampleAreaStageImpl sampleAreaStage;
  private ServiceRegistration<?> sampleAreaRegistration;

  public SaintStageManager(XYStage<?> xyStage, SaintProperties properties) {
    this.xyStage = xyStage;
    this.xyStageController = new DeviceDependency<>(xyStage, 5, SECONDS, d -> processState());

    this.samplePlateStage = new SamplePlateStageImpl(properties, this);
    this.sampleAreaStage = new SampleAreaStageImpl(properties, this);
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

    open();
  }

  @Deactivate
  public void deactivate() {
    close();

    if (sampleAreaRegistration != null) {
      sampleAreaRegistration.unregister();
      sampleAreaRegistration = null;
    }
    if (samplePlateRegistration != null) {
      samplePlateRegistration.unregister();
      samplePlateRegistration = null;
    }
  }

  public synchronized void open() {
    xyStageController.open();
    observer = xyStage.sampleState().value().observe(s -> processState());
  }

  public synchronized void close() {
    observer.cancel();
    xyStageController.close();
  }

  XYStage<?> xyStage() {
    return xyStage;
  }

  public SamplePlateStageImpl samplePlateStage() {
    return samplePlateStage;
  }

  public SampleAreaStageImpl sampleAreaStage() {
    return sampleAreaStage;
  }

  synchronized void processState() {
    var requestedSamplePlateState = samplePlateStage.requestedSampleState().get();
    var requestedSampleAreaState = sampleAreaStage.requestedSampleState().get();

    if (isAtAnalysis()) {
      /*
       * Our sample area device is acquired and ready for analysis, so it is given
       * command of the XY stage controller.
       */

      if (requestedSampleAreaState instanceof Ready<?>) {

      } else if (requestedSampleAreaState instanceof Analysis<?>) {
        var analysisPosition = ((Analysis<SampleArea>) requestedSamplePlateState)
            .position()
            .center()
            .add(((Analysis<XYCoordinate<Length>>) requestedSampleAreaState).position());

        requestXyState(
            SampleState.analysis(analysisPosition),
            this.sampleAreaStage::requestReached,
            this.sampleAreaStage::requestFailed);
      }
    } else {
      /*
       * Our sample area device is released or ready for exchange, so our sample plate
       * device is given command of the XY stage controller.
       */

      requestXyState(
          mapRequestedSampleState(requestedSamplePlateState, SampleArea::center),
          this.samplePlateStage::requestReached,
          this.samplePlateStage::requestFailed);
    }
  }

  @SuppressWarnings("unchecked")
  private <T, U> RequestedSampleState<U> mapRequestedSampleState(
      RequestedSampleState<T> requestedSampleState,
      Function<? super T, ? extends U> mapping) {
    if (requestedSampleState instanceof Analysis<?>) {
      return SampleState.analysis(mapping.apply(((Analysis<T>) requestedSampleState).position()));
    } else {
      return (RequestedSampleState<U>) requestedSampleState;
    }
  }

  private boolean requestXyState(
      RequestedSampleState<XYCoordinate<Length>> state,
      Runnable success,
      Runnable failure) {
    xyStageController.getController().ifPresentOrElse(control -> {
      if (!xyStage.requestedSampleState().isEqual(state)) {
        control.request(state);
      }
      success.run();
    }, () -> {
      failure.run();
    });
    return xyStage.sampleState().isEqual(state);
  }

  private boolean isAtAnalysis() {
    return samplePlateStage.sampleState().isMatching(s -> s instanceof Analysis<?>)
        && (samplePlateStage.requestedSampleState().isMatching(s -> s instanceof Analysis<?>)
            || (sampleAreaStage.status().isEqual(UNAVAILABLE)
                && !sampleAreaStage.sampleState().isMatching(r -> r instanceof Exchange<?>)));
  }
}
