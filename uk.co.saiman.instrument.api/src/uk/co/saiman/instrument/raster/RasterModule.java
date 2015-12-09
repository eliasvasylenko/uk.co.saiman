package uk.co.saiman.instrument.raster;

import java.util.Set;

import uk.co.saiman.instrument.HardwareModule;

public interface RasterModule extends HardwareModule {
	Set<RasterMode> availableRasterModes();

	RasterMode getRasterMode();

	void setRasterMode(RasterMode mode);

	void setRasterSize(int width, int height);

	int getRasterWidth();

	int getRasterHeight();

	default int getRasterLength() {
		return getRasterHeight() * getRasterWidth();
	}

	void startRaster();
}
