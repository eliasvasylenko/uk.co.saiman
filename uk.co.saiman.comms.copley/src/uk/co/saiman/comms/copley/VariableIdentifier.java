package uk.co.saiman.comms.copley;

import uk.co.saiman.comms.Bits;
import uk.co.saiman.comms.BitsConversion;

public class VariableIdentifier {
	@Bits(0)
	public byte variableID;

	@Bits(12)
	public boolean bank;

	@Bits(13)
	@BitsConversion(size = 3)
	public byte axis;
}
