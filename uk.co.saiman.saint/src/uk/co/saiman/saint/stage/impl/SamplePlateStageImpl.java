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
import uk.co.saiman.instrument.sample.SampleDevice;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.instrument.stage.XYStageController;
import uk.co.saiman.instrument.virtual.AbstractingDevice;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.properties.PropertyLoader;
import uk.co.saiman.saint.SaintProperties;
import uk.co.saiman.saint.stage.SampleArea;
import uk.co.saiman.saint.stage.SampleAreaStage;
import uk.co.saiman.saint.stage.SamplePlateStage;
import uk.co.saiman.saint.stage.SamplePlateStageController;

/**
 * An implementation of a stage for the Saint instrument which is backed by an
 * {@link XYStage} implementation. The backing stage must be rectangular, with
 * an accessible area over the Saint sample plate.
 * 
 * @author Elias N Vasylenko
 *
 */
@Designate(ocd = SamplePlateStageImpl.SaintStageConfiguration.class, factory = true)
@Component(name = SamplePlateStageImpl.CONFIGURATION_PID, configurationPid = SamplePlateStageImpl.CONFIGURATION_PID, configurationPolicy = REQUIRE, service = {
    SamplePlateStage.class,
    SampleDevice.class,
    Stage.class,
    Device.class })
public class SamplePlateStageImpl extends AbstractingDevice<SamplePlateStageController>
    implements SamplePlateStage {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.saint";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(id = CONFIGURATION_PID, name = "SAINT Stage Configuration", description = "The configuration for a modular stage composed of an x axis and a y axis")
  public @interface SaintStageConfiguration {
    String name();
  }

  private final XYStage<?> xyStage;
  private final DeviceDependency<? extends XYStageController> xyStageController;

  private final ObservableProperty<XYCoordinate<Length>> requestedOffset;
  private final ObservableProperty<SampleArea> requestedLocation;
  private final ObservableProperty<SampleArea> actualLocation;

  private final SampleAreaStageImpl sampleArea;
  private ServiceRegistration<?> sampleAreaRegistration;

  @Activate
  public SamplePlateStageImpl(
      BundleContext context,
      @Reference(name = "xyStage") XYStage<?> xyStage,
      SaintStageConfiguration configuration,
      @Reference PropertyLoader propertyLoader) {
    this(configuration.name(), xyStage, propertyLoader.getProperties(SaintProperties.class));
    this.sampleAreaRegistration = context
        .registerService(
            new String[] {
                SampleAreaStage.class.getName(),
                Stage.class.getName(),
                SampleDevice.class.getName(),
                Device.class.getName() },
            this.sampleArea,
            new Hashtable<>());
  }

  public SamplePlateStageImpl(String name, XYStage<?> xyStage, SaintProperties properties) {
    super(name, xyStage.getInstrumentRegistration().getInstrument());
    this.xyStage = xyStage;
    this.xyStageController = new DeviceDependency<>(xyStage, true);

    this.requestedOffset = ObservableProperty.over(NullPointerException::new);
    this.requestedLocation = ObservableProperty.over(NullPointerException::new);
    this.actualLocation = ObservableProperty.over(NullPointerException::new);

    this.sampleArea = new SampleAreaStageImpl(this, properties);
  }

  @Deactivate
  public void deactivate() {
    if (sampleAreaRegistration != null) {
      sampleAreaRegistration.unregister();
      sampleAreaRegistration = null;
    }
  }

  @Override
  public ObservableValue<SampleState> sampleState() {
    return xyStage.sampleState();
  }

  @Override
  public boolean isLocationReachable(SampleArea location) {
    return xyStage.isLocationReachable(location.center().add(location.lowerBound()))
        && xyStage.isLocationReachable(location.center().add(location.upperBound()));
  }

  @Override
  public ObservableValue<XYCoordinate<Length>> requestedOffset() {
    return requestedOffset;
  }

  private void setRequestedOffset(XYCoordinate<Length> offset) {
    var offsetFromLowerBound = requestedLocation
        .tryGet()
        .map(SampleArea::lowerBound)
        .map(lowerBound -> offset.subtract(lowerBound))
        .orElse(offset);

    var offsetFromUpperBound = requestedLocation
        .tryGet()
        .map(SampleArea::upperBound)
        .map(upperBound -> offset.subtract(upperBound))
        .orElse(offset);

    if (offsetFromLowerBound.getX().getValue().doubleValue() < 0
        || offsetFromLowerBound.getY().getValue().doubleValue() < 0
        || offsetFromUpperBound.getX().getValue().doubleValue() > 0
        || offsetFromUpperBound.getY().getValue().doubleValue() > 0) {
      throw new IllegalArgumentException();
    }
    requestedOffset.set(offset);
  }

  @Override
  public ObservableValue<SampleArea> requestedLocation() {
    return requestedLocation;
  }

  private void setRequestedLocation(SampleArea location) {
    var offsetFromLowerBound = location
        .center()
        .add(location.lowerBound())
        .subtract(getLowerBound());
    var offsetFromUpperBound = location
        .center()
        .add(location.upperBound())
        .subtract(getUpperBound());

    if (offsetFromLowerBound.getX().getValue().doubleValue() < 0
        || offsetFromLowerBound.getY().getValue().doubleValue() < 0
        || offsetFromUpperBound.getX().getValue().doubleValue() > 0
        || offsetFromUpperBound.getY().getValue().doubleValue() > 0) {
      throw new IllegalArgumentException();
    }
    requestedLocation.set(location);
  }

  @Override
  public ObservableValue<SampleArea> actualLocation() {
    return actualLocation;
  }

  public ObservableValue<XYCoordinate<Length>> requestedCoordinate() {
    return xyStage.requestedLocation();
  }

  public ObservableValue<XYCoordinate<Length>> actualCoordinate() {
    return xyStage.actualLocation();
  }

  private XYCoordinate<Length> getRequestedXYStageLocation() {
    return requestedLocation.get().center().add(requestedOffset.get());
  }

  @Override
  protected SamplePlateStageController acquireControl(AbstractingControlLock lock) {
    return new SamplePlateStageController() {
      @Override
      public void requestExchange() {
        lock.getController(xyStageController).requestExchange();
      }

      @Override
      public void requestAnalysis(SampleArea location) {
        lock.run(() -> setRequestedLocation(location));
        lock.getController(xyStageController).requestAnalysis(getRequestedXYStageLocation());
      }

      @Override
      public void requestReady() {
        lock.getController(xyStageController).requestReady();
      }

      @Override
      public void close() {
        lock.close();
      }

      @Override
      public SampleState awaitRequest(long timeout, TimeUnit unit) {
        return lock.getController(xyStageController).awaitRequest(timeout, unit);
      }

      @Override
      public SampleState awaitReady(long timeout, TimeUnit unit) {
        return lock.getController(xyStageController).awaitReady(timeout, unit);
      }

      @Override
      public void requestOffset(XYCoordinate<Length> offset) {
        lock.run(() -> setRequestedOffset(offset));
        lock.getController(xyStageController).requestAnalysis(getRequestedXYStageLocation());
      }
    };
  }

  @Override
  public XYCoordinate<Length> getLowerBound() {
    return xyStage.getLowerBound();
  }

  @Override
  public XYCoordinate<Length> getUpperBound() {
    return xyStage.getUpperBound();
  }

  @Override
  public SampleAreaStage sampleArea() {
    return sampleArea;
  }
}
