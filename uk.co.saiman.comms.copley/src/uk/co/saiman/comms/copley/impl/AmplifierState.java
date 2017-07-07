package uk.co.saiman.comms.copley.impl;

import uk.co.saiman.comms.Bits;
import uk.co.saiman.comms.Bytes;
import uk.co.saiman.comms.copley.AmplifierMode;

@Bytes(count = 2)
public class AmplifierState {
	@Bits(0)
	public AmplifierMode mode;
}
