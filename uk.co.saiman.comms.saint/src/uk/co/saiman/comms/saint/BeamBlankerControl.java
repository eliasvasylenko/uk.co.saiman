package uk.co.saiman.comms.saint;

import uk.co.saiman.comms.Bits;
import uk.co.saiman.comms.BitsConversion;

public class BeamBlankerControl {
	@Bits(0)
	@BitsConversion(size = 8)
	int delay;
}
