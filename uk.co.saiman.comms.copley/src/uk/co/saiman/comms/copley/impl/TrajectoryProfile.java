package uk.co.saiman.comms.copley.impl;

import uk.co.saiman.comms.Bits;
import uk.co.saiman.comms.BitsConversion;
import uk.co.saiman.comms.copley.TrajectoryProfileMode;

public class TrajectoryProfile {
	@Bits(0)
	@BitsConversion(size = 3)
	TrajectoryProfileMode mode;

	@Bits(8)
	boolean relative;
}
