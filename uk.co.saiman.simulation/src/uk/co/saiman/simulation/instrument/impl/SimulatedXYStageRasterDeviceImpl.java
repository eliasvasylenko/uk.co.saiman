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
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.instrument.impl;

import static java.util.Arrays.asList;
import static uk.co.saiman.instrument.HardwareConnection.CONNECTED;
import static uk.co.saiman.instrument.raster.RasterPattern.RasterPatterns.values;
import static uk.co.strangeskies.observable.ObservableValue.immutableOver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.chemistry.ChemicalComposition;
import uk.co.saiman.instrument.HardwareConnection;
import uk.co.saiman.instrument.HardwareDevice;
import uk.co.saiman.instrument.raster.RasterDevice;
import uk.co.saiman.instrument.raster.RasterPattern;
import uk.co.saiman.instrument.raster.RasterPosition;
import uk.co.saiman.instrument.stage.StageDimension;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.saiman.simulation.SimulationProperties;
import uk.co.saiman.simulation.instrument.SimulatedDevice;
import uk.co.saiman.simulation.instrument.SimulatedRasterDevice;
import uk.co.saiman.simulation.instrument.SimulatedSample;
import uk.co.saiman.simulation.instrument.SimulatedSampleDevice;
import uk.co.saiman.simulation.instrument.SimulatedSampleImage;
import uk.co.saiman.simulation.instrument.SimulatedSampleImageDevice;
import uk.co.strangeskies.observable.HotObservable;
import uk.co.strangeskies.observable.Observable;
import uk.co.strangeskies.observable.ObservableValue;
import uk.co.strangeskies.text.properties.PropertyLoader;

@Component
public class SimulatedXYStageRasterDeviceImpl
    implements RasterDevice, SimulatedRasterDevice, SimulatedSampleImageDevice, XYStageDevice,
    HardwareDevice, SimulatedSampleDevice, SimulatedDevice {
  @Reference
  PropertyLoader loader;
  SimulationProperties text;

  private final Set<RasterPattern> rasterModes;
  private RasterPattern rasterMode;
  private Iterator<RasterPosition> rasterOperation;
  private int rasterCounter;
  private RasterPosition rasterPosition;
  private int rasterWidth;
  private int rasterHeight;
  private int rasterDwell;
  private final HotObservable<RasterPosition> rasterListeners;

  private ChemicalComposition redChemical;
  private ChemicalComposition greenChemical;
  private ChemicalComposition blueChemical;
  private SimulatedSampleImage sampleImage;
  private SimulatedSample signalSample;

  public SimulatedXYStageRasterDeviceImpl() {
    rasterModes = new HashSet<>(asList(values()));

    rasterWidth = 1;
    rasterHeight = 1;

    rasterListeners = new HotObservable<>();

    signalSample = new SimulatedSample() {
      @Override
      public Map<ChemicalComposition, Double> chemicalIntensities() {
        Map<ChemicalComposition, Double> sampleChemicals = new HashMap<>();
        int x = rasterPosition.getX();
        int y = rasterPosition.getY();

        sampleChemicals.put(redChemical, sampleImage.getRed(x, y));
        sampleChemicals.put(greenChemical, sampleImage.getGreen(x, y));
        sampleChemicals.put(blueChemical, sampleImage.getBlue(x, y));
        return sampleChemicals;
      }
    };
  }

  @Override
  public void setRedChemical(ChemicalComposition redChemical) {
    this.redChemical = redChemical;
  }

  @Override
  public void setGreenChemical(ChemicalComposition greenChemical) {
    this.greenChemical = greenChemical;
  }

  @Override
  public void setBlueChemical(ChemicalComposition blueChemical) {
    this.blueChemical = blueChemical;
  }

  @Override
  public void setSampleImage(SimulatedSampleImage sampleImage) {
    this.sampleImage = sampleImage;
  }

  @Activate
  void activate() {
    text = loader.getProperties(SimulationProperties.class);
  }

  @Override
  public String getName() {
    return text.xyRasterStageSimulationDeviceName().get();
  }

  @Override
  public Set<RasterPattern> availableRasterModes() {
    return rasterModes;
  }

  @Override
  public RasterPattern getRasterPattern() {
    return rasterMode;
  }

  @Override
  public void setRasterPattern(RasterPattern mode) {
    rasterModes.add(mode);
    rasterMode = mode;
  }

  @Override
  public void setRasterSize(int width, int height) {
    this.rasterWidth = width;
    this.rasterHeight = height;
  }

  @Override
  public int getRasterWidth() {
    return rasterWidth;
  }

  @Override
  public int getRasterHeight() {
    return rasterHeight;
  }

  @Override
  public void startRasterOperation() {
    rasterOperation = rasterMode.getPositionIterator(rasterWidth, rasterHeight);
  }

  @Override
  public RasterPosition getRasterPosition() {
    return rasterPosition;
  }

  protected void advanceRasterPosition() {
    if (rasterOperation == null) {
      rasterPosition = new RasterPosition(0, 0, 0);
    } else if (rasterCounter++ % rasterDwell == 0) {
      rasterPosition = rasterOperation.next();
      rasterListeners.next(rasterPosition);

      if (rasterCounter == rasterWidth * rasterHeight * rasterDwell) {
        rasterOperation = null;
      }
    }
  }

  @Override
  public boolean isOperating() {
    return rasterOperation != null;
  }

  @Override
  public Observable<RasterPosition> rasterPositionEvents() {
    return rasterListeners;
  }

  @Override
  public StageDimension<Length> getXAxis() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StageDimension<Length> getYAxis() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SimulatedSample getNextSample() {
    advanceRasterPosition();

    return signalSample;
  }

  @Override
  public void setRasterDwell(int dwell) {
    this.rasterDwell = dwell;
  }

  @Override
  public int getRasterDwell() {
    return rasterDwell;
  }

  @Override
  public SimulatedSampleDevice getSampleDevice() {
    return this;
  }

  @Override
  public ObservableValue<HardwareConnection> connectionState() {
    return immutableOver(CONNECTED);
  }
}
