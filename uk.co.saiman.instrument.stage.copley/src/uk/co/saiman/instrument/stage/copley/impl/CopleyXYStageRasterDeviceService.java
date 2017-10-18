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
package uk.co.saiman.instrument.stage.copley.impl;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.raster.RasterDevice;
import uk.co.saiman.instrument.raster.RasterPattern;
import uk.co.saiman.instrument.raster.RasterPosition;
import uk.co.saiman.instrument.stage.StageDimension;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.saiman.instrument.stage.copley.CopleyXYStageDevice;
import uk.co.saiman.instrument.stage.copley.CopleyXYStageDevice.CopleyXYStageConfiguration;
import uk.co.saiman.instrument.stage.copley.impl.CopleyXYStageRasterDeviceService.CopleyXYStageRasterConfiguration;
import uk.co.saiman.mathematics.Interval;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.text.properties.PropertyLoader;

/**
 * A Copley motor XY stage which uses stage position offset as a rastering
 * mechanism.
 * <p>
 * The start position of the raster is taken from the
 * {@link StageDimension#requestedPosition() requested position} at the time the
 * raster begins. If a position is manually requested during a raster operation,
 * the operation fails and the {@link #rasterPositionEvents() raster position
 * observable} sends a terminal failure event to its observers.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = CopleyXYStageRasterConfiguration.class, factory = true)
@Component(
    configurationPid = CopleyXYStageRasterDeviceService.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class CopleyXYStageRasterDeviceService extends CopleyXYStageDevice
    implements RasterDevice, XYStageDevice {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.copley.xy.rastering";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Copley XY Stage Raster Configuration",
      description = "An implementation of a rastering XY stage device interface based on copley motor hardware")
  public @interface CopleyXYStageRasterConfiguration {
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

  @Reference
  Instrument instrument;

  @Reference
  PropertyLoader loader;

  @Reference
  Units units;

  @Reference
  CopleyComms comms;

  private RasterPattern rasterPattern;
  private RasterPosition rasterPosition;
  private Quantity<Length> rasterResolutionX;
  private Quantity<Length> rasterResolutionY;

  @Override
  public String getName() {
    return getProperties().copleyXYStageRasterName().get();
  }

  @Activate
  void activate(
      CopleyXYStageRasterConfiguration rasterConfiguration,
      CopleyXYStageConfiguration configuration) {
    initialize(instrument, comms, loader);
    configure(configuration, units);
  }

  @Modified
  void modified(
      CopleyXYStageRasterConfiguration rasterConfiguration,
      CopleyXYStageConfiguration configuration) {
    configure(configuration, units);
  }

  public void setRasterPattern(RasterPattern mode) {
    rasterPattern = mode;
  }

  public void setRasterResolution(Quantity<Length> x, Quantity<Length> y) {
    this.rasterResolutionX = x;
    this.rasterResolutionY = y;
  }

  public Quantity<Length> getRasterResolutionX() {
    return rasterResolutionX;
  }

  public Quantity<Length> getRasterResolutionY() {
    return rasterResolutionY;
  }

  @Override
  public int getRasterWidth() {
    // TODO Auto-generated method stub
    return 1;
  }

  @Override
  public int getRasterHeight() {
    // TODO Auto-generated method stub
    return 1;
  }

  @Override
  public boolean isRasterOperating() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public RasterPosition getRasterPosition() {
    return rasterPosition;
  }

  @Override
  public Observable<RasterPosition> rasterPositionEvents() {
    // TODO Auto-generated method stub
    return null;
  }

  class RasteringAxis implements StageDimension<Length> {
    private final StageDimension<Length> component;

    public RasteringAxis(StageDimension<Length> component) {
      this.component = component;
    }

    @Override
    public Unit<Length> getUnit() {
      return component.getUnit();
    }

    @Override
    public Interval<Quantity<Length>> getBounds() {
      return component.getBounds();
    }

    @Override
    public ObservableProperty<Quantity<Length>> requestedPosition() {
      return component.requestedPosition();
    }

    @Override
    public ObservableValue<Quantity<Length>> actualPosition() {
      return component.actualPosition();
    }
  }
}
