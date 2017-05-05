package uk.co.saiman.comms.impl;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.comms.BitArray;
import uk.co.saiman.comms.BitConverter;

@Component
public class IntBitConverter implements BitConverter<Integer> {
	@Override
	public Class<Integer> getType() {
		return int.class;
	}

	@Override
	public int getDefaultBits() {
		return Integer.SIZE;
	}

	@Override
	public Integer toObject(BitArray bits) {
		return bits.toInt();
	}

	@Override
	public BitArray toBits(Integer object, int size) {
		return BitArray.fromInt(object).resize(size);
	}
}
