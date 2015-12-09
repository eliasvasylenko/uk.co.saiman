package uk.co.saiman.instrument.acquisition;

import java.util.List;

import uk.co.strangeskies.mathematics.Range;

public interface AcquisitionGates {
	AcquisitionModule acquisition();

	double getStartTime();

	void setStartTime(double newStartTime);

	List<Range<Double>> getPassGates();

	void setPassGates(List<Range<Double>> gates);

	List<Range<Double>> getFailGates();

	void setFailGates(List<Range<Double>> gates);
}
