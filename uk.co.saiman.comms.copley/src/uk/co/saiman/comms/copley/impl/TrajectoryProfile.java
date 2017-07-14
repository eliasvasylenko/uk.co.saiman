package uk.co.saiman.comms.copley.impl;

import java.lang.reflect.Type;

import uk.co.saiman.comms.BitConverter;
import uk.co.saiman.comms.BitConverterFactory;
import uk.co.saiman.comms.Bit;
import uk.co.saiman.comms.Bits;
import uk.co.saiman.comms.PrimitiveBitConverters;
import uk.co.saiman.comms.copley.TrajectoryProfileMode;

public class TrajectoryProfile {
	@Bit(0)
	@Bits(value = 3)
	public TrajectoryProfileMode mode;

	@Bit(8)
	public boolean relative;
}

class TrajectoryProfileModeConverter implements BitConverterFactory {
	@Override
	public BitConverter<TrajectoryProfileMode> getBitConverter(Type type) {
		return new PrimitiveBitConverters.Ints()
				.map(TrajectoryProfileMode::forCode, TrajectoryProfileMode::getCode);
	}
}
