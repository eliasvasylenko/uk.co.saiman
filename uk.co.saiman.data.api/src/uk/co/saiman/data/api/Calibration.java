package uk.co.saiman.data.api;

public interface Calibration {
	double calibrate(double from);

	double decalibrate(double from);
}
