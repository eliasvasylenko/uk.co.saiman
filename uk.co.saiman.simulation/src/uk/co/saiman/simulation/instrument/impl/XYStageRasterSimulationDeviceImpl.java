/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
import static uk.co.saiman.instrument.raster.RasterPattern.RasterPatterns.values;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.chemistry.ChemicalComposition;
import uk.co.saiman.instrument.HardwareDevice;
import uk.co.saiman.instrument.raster.RasterDevice;
import uk.co.saiman.instrument.raster.RasterPattern;
import uk.co.saiman.instrument.raster.RasterPosition;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.saiman.simulation.SimulationText;
import uk.co.saiman.simulation.instrument.ImageSampleDeviceSimulation;
import uk.co.saiman.simulation.instrument.SampleDeviceSimulation;
import uk.co.saiman.simulation.instrument.SampleImage;
import uk.co.saiman.simulation.instrument.SimulatedDevice;
import uk.co.saiman.simulation.instrument.SimulatedSample;
import uk.co.strangeskies.text.properties.PropertyLoader;
import uk.co.strangeskies.utilities.BufferingListener;
import uk.co.strangeskies.utilities.Observable;
import uk.co.strangeskies.utilities.ObservableImpl;

@Component
public class XYStageRasterSimulationDeviceImpl implements RasterDevice, ImageSampleDeviceSimulation, XYStageDevice,
		HardwareDevice, SampleDeviceSimulation, SimulatedDevice {
	@Reference
	PropertyLoader loader;
	SimulationText text;

	private final ObservableImpl<Exception> errors;

	private final Set<RasterPattern> rasterModes;
	private RasterPattern rasterMode;
	private Iterator<RasterPosition> rasterOperation;
	private int rasterCounter;
	private RasterPosition rasterPosition;
	private int rasterWidth;
	private int rasterHeight;
	private int rasterDwell;
	private final BufferingListener<RasterPosition> singleRasterListeners;
	private final BufferingListener<RasterPosition> rasterListeners;

	private ChemicalComposition redChemical;
	private ChemicalComposition greenChemical;
	private ChemicalComposition blueChemical;
	private SampleImage sampleImage;
	private SimulatedSample signalSample;

	public XYStageRasterSimulationDeviceImpl() {
		errors = new ObservableImpl<>();

		rasterModes = new HashSet<>(asList(values()));

		rasterWidth = 1;
		rasterHeight = 1;

		singleRasterListeners = new BufferingListener<>();
		rasterListeners = new BufferingListener<>();

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
	public void setSampleImage(SampleImage sampleImage) {
		this.sampleImage = sampleImage;
	}

	@Activate
	void activate() {
		text = loader.getProperties(SimulationText.class);
	}

	@Override
	public String getName() {
		return text.xyRasterStageSimulationDeviceName().get();
	}

	@Override
	public void abortOperation() {
		// singleAcquisitionListeners.clearObservers();

		// acquiringCounter = 0;
	}

	@Override
	public Observable<Exception> errors() {
		return errors;
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
			rasterListeners.accept(rasterPosition);
			singleRasterListeners.accept(rasterPosition);

			if (!rasterOperation.hasNext()) {
				if (rasterCounter == 0) {
					singleRasterListeners.clearObservers();
				}
			}

			if (rasterCounter == rasterWidth * rasterHeight * rasterDwell) {
				rasterOperation = null;
			}
		}
	}

	@Override
	public Observable<RasterPosition> nextOperationRasterPositionEvents() {
		return singleRasterListeners;
	}

	@Override
	public Observable<RasterPosition> rasterPositionEvents() {
		return rasterListeners;
	}

	@Override
	public Quantity<Length> getStageWidth() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quantity<Length> getStageHeight() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void requestStageOffset(Quantity<Length> x, Quantity<Length> y) {
		// TODO Auto-generated method stub

	}

	@Override
	public Quantity<Length> getRequestedStageX() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quantity<Length> getRequestedStageY() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quantity<Length> getActualStageX() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quantity<Length> getActualStageY() {
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
}
