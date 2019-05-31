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

import static uk.co.saiman.measurement.Units.metre;

import java.util.concurrent.TimeUnit;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.DeviceImpl;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.saint.SaintProperties;
import uk.co.saiman.saint.stage.SampleArea;
import uk.co.saiman.saint.stage.SampleAreaStage;
import uk.co.saiman.saint.stage.SampleAreaStageController;
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
public class SampleAreaStageImpl extends DeviceImpl<SampleAreaStageController>
    implements SampleAreaStage {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.saint";

  private static final XYCoordinate<Length> ZERO = new XYCoordinate<>(metre().getUnit(), 0, 0);

  private final SamplePlateStageImpl samplePlate;

  private final ObservableProperty<XYCoordinate<Length>> requestedLocation;
  private final ObservableProperty<XYCoordinate<Length>> actualLocation;

  public SampleAreaStageImpl(SamplePlateStageImpl samplePlate, SaintProperties properties) {
    super(
        properties.sampleAreaStageDeviceName().toString(),
        samplePlate.getInstrumentRegistration().getInstrument());
    this.samplePlate = samplePlate;
    this.requestedLocation = ObservableProperty.over(ZERO);
    this.actualLocation = ObservableProperty.over(ZERO);
  }

  @Override
  public ObservableValue<SampleState> sampleState() {
    return samplePlate.sampleState();
  }

  @Override
  public XYCoordinate<Length> getLowerBound() {
    return samplePlate.requestedLocation().tryGet().map(SampleArea::lowerBound).orElse(ZERO);
  }

  @Override
  public XYCoordinate<Length> getUpperBound() {
    return samplePlate.requestedLocation().tryGet().map(SampleArea::upperBound).orElse(ZERO);
  }

  @Override
  public boolean isLocationReachable(XYCoordinate<Length> location) {
    return samplePlate
        .requestedLocation()
        .tryGet()
        .map(area -> area.isLocationReachable(location))
        .orElse(ZERO.equals(location));
  }

  @Override
  public ObservableValue<XYCoordinate<Length>> requestedLocation() {
    return requestedLocation;
  }

  @Override
  public ObservableValue<XYCoordinate<Length>> actualLocation() {
    return actualLocation;
  }

  @Override
  protected SampleAreaStageController acquireControl(ControlLock lock) {
    return new SampleAreaStageController() {

      @Override
      public void requestExchange() {
        // TODO Auto-generated method stub

      }

      @Override
      public void requestReady() {
        // TODO Auto-generated method stub

      }

      @Override
      public void requestAnalysis(XYCoordinate<Length> location) {
        // TODO Auto-generated method stub

      }

      @Override
      public SampleState awaitRequest(long timeout, TimeUnit unit) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public SampleState awaitReady(long timeout, TimeUnit unit) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public void close() {
        // TODO Auto-generated method stub

      }

      @Override
      public SamplePlateStageController samplePlateStageController() {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }

  @Override
  public SamplePlateStage samplePlateStage() {
    return samplePlate;
  }
}
