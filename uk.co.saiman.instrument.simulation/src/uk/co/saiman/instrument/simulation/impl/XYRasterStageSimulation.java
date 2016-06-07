package uk.co.saiman.instrument.simulation.impl;

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
import uk.co.saiman.instrument.simulation.SampleImage;
import uk.co.saiman.instrument.simulation.SampleSimulation;
import uk.co.saiman.instrument.simulation.SimulationDevice;
import uk.co.saiman.instrument.simulation.SimulationSample;
import uk.co.saiman.instrument.simulation.SimulationText;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.strangeskies.utilities.BufferingListener;
import uk.co.strangeskies.utilities.Observable;
import uk.co.strangeskies.utilities.ObservableImpl;
import uk.co.strangeskies.utilities.text.Localizer;

@Component
public class XYRasterStageSimulation extends SimulationDevice
		implements RasterDevice, XYStageDevice, HardwareDevice, SampleSimulation {
	@Reference
	Localizer localizer;
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

	private SampleImage sampleImage;
	private SimulationSample signalSample;
	private Map<ChemicalComposition, Double> sampleChemicals;

	public XYRasterStageSimulation() {
		errors = new ObservableImpl<>();

		rasterModes = new HashSet<>(asList(values()));

		rasterWidth = 1;
		rasterHeight = 1;

		singleRasterListeners = new BufferingListener<>();
		rasterListeners = new BufferingListener<>();

		signalSample = new SimulationSample() {
			@Override
			public Map<ChemicalComposition, Double> chemicalIntensities() {
				return sampleChemicals;
			}
		};
		sampleChemicals = new HashMap<>();
	}

	public void setSampleImage(SampleImage sampleImage) {
		this.sampleImage = sampleImage;
	}

	@Activate
	void activate() {
		text = localizer.getLocalization(SimulationText.class);
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
	public SimulationSample getNextSample() {
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
