package uk.co.saiman.instrument.simulation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.instrument.HardwareDevice;
import uk.co.saiman.instrument.raster.RasterDevice;
import uk.co.saiman.instrument.raster.RasterMode;
import uk.co.saiman.instrument.raster.RasterPosition;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.strangeskies.utilities.BufferingListener;
import uk.co.strangeskies.utilities.Observable;
import uk.co.strangeskies.utilities.text.Localizer;

@Component
public class XYRasterStageSimulation extends SimulationDevice implements RasterDevice, XYStageDevice, HardwareDevice {
	@Reference
	Localizer localizer;
	SimulationText text;

	private final Set<RasterMode> rasterModes;

	private RasterMode rasterMode;
	private int width;
	private int height;

	private Iterator<RasterPosition> rasterOperation;
	private RasterPosition currentPosition;

	private final BufferingListener<SampledContinuousFunction> singleRasterListeners;
	private final BufferingListener<SampledContinuousFunction> rasterListeners;

	public XYRasterStageSimulation() {
		rasterModes = new HashSet<>();
		setRasterMode(RasterMode.SNAKE);

		width = 1;
		height = 1;

		singleRasterListeners = new BufferingListener<>();
		rasterListeners = new BufferingListener<>();
	}

	@Activate
	void activate() {
		text = localizer.getLocalization(SimulationText.class);
	}

	@Override
	public String getName() {
		return text.xyRasterStageDeviceName().get();
	}

	@Override
	public void abortOperation() {
		//singleAcquisitionListeners.clearObservers();

		//acquiringCounter = 0;
	}

	@Override
	public void addErrorListener(Consumer<Exception> exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<RasterMode> availableRasterModes() {
		return rasterModes;
	}

	@Override
	public RasterMode getRasterMode() {
		return rasterMode;
	}

	@Override
	public void setRasterMode(RasterMode mode) {
		rasterModes.add(mode);
		rasterMode = mode;
	}

	@Override
	public void setRasterSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public int getRasterWidth() {
		return width;
	}

	@Override
	public int getRasterHeight() {
		return height;
	}

	@Override
	public void startRasterOperation() {
		rasterOperation = rasterMode.getPositionIterator(width, height);
	}

	@Override
	public RasterPosition getRasterPosition() {
		return currentPosition;
	}

	protected void advanceRasterPosition() {
		currentPosition = rasterOperation.next();
		//singleRasterListeners.accept(acquisitionData);

		if (!rasterOperation.hasNext()) {
		//	if (acquiringCounter == 0) {
		//		singleRasterListeners.clearObservers();
		//	}
		}
	}

	@Override
	public Observable<RasterPosition> nextOperationRasterPositionEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Observable<RasterPosition> rasterPositionEvents() {
		// TODO Auto-generated method stub
		return null;
	}
}
