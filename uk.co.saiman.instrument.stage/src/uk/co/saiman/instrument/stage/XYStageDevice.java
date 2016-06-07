package uk.co.saiman.instrument.stage;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import uk.co.saiman.instrument.HardwareDevice;

public interface XYStageDevice extends HardwareDevice {
	Quantity<Length> getStageWidth();

	Quantity<Length> getStageHeight();

	void requestStageOffset(Quantity<Length> x, Quantity<Length> y);

	Quantity<Length> getRequestedStageX();

	Quantity<Length> getRequestedStageY();

	Quantity<Length> getActualStageX();

	Quantity<Length> getActualStageY();
}
