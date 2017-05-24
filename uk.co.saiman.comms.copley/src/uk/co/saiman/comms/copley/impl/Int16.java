package uk.co.saiman.comms.copley.impl;

import uk.co.saiman.comms.Bits;
import uk.co.saiman.comms.BitsConversion;

public class Int16 {
	@Bits(0)
	@BitsConversion(size = 16)
	public int value;
}
