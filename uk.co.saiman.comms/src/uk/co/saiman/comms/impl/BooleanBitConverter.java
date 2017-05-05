package uk.co.saiman.comms.impl;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.comms.BitArray;
import uk.co.saiman.comms.BitConverter;

@Component
public class BooleanBitConverter implements BitConverter<Boolean> {
	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}

	@Override
	public int getDefaultBits() {
		return 1;
	}

	@Override
	public Boolean toObject(BitArray bits) {
		return bits.resize(-1).get(0);
	}

	@Override
	public BitArray toBits(Boolean object, int size) {
		return new BitArray(1).with(0, object).resize(-size);
	}
}
