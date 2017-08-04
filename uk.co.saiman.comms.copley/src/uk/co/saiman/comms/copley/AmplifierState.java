package uk.co.saiman.comms.copley;

import java.lang.reflect.Type;

import uk.co.saiman.comms.Bit;
import uk.co.saiman.comms.BitConverter;
import uk.co.saiman.comms.BitConverterFactory;
import uk.co.saiman.comms.Bits;
import uk.co.saiman.comms.Bytes;
import uk.co.saiman.comms.PrimitiveBitConverters;

@Bytes(2)
public class AmplifierState {
	@Bit(0)
	@Bits(converter = AmplifierModeConverter.class)
	public AmplifierMode mode;
}

class AmplifierModeConverter implements BitConverterFactory {
	@Override
	public BitConverter<AmplifierMode> getBitConverter(Type type) {
		return new PrimitiveBitConverters.Ints()
				.map(AmplifierMode::forCode, AmplifierMode::getCode)
				.withDefaultBits(16);
	}
}
