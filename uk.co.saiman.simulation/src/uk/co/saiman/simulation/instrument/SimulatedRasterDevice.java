package uk.co.saiman.simulation.instrument;

import uk.co.saiman.instrument.raster.RasterDevice;

public interface SimulatedRasterDevice extends RasterDevice {
	SimulatedSampleDevice getSampleDevice();
}
