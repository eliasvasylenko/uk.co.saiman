package uk.co.saiman.instrument.simulation;

public interface SampleImage {
	int getWidth();

	int getHeight();

	double getRed(int x, int y);

	double getGreen(int x, int y);

	double getBlue(int x, int y);
}
