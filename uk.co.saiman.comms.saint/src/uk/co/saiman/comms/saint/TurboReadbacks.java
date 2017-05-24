package uk.co.saiman.comms.saint;

import uk.co.saiman.comms.Bits;
import uk.co.saiman.comms.BitsConversion;

public class TurboReadbacks {
	@Bits(16)
	@BitsConversion(size = 16)
	public int speed;
	@Bits(8)
	@BitsConversion(size = 8)
	public int temperature;
	@Bits(0)
	@BitsConversion(size = 8)
	public int poduleTemperature;
}
