package uk.co.saiman.comms.impl;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.comms.BitArray;
import uk.co.saiman.comms.BitConverter;

@Component
public class ByteBitConverter implements BitConverter<Byte> {
	@Override
	public Class<Byte> getType() {
		return byte.class;
	}

	@Override
	public int getDefaultBits() {
		return Byte.SIZE;
	}

	@Override
	public Byte toObject(BitArray bits) {
		return bits.toByte();
	}

	@Override
	public BitArray toBits(Byte object, int size) {
		return BitArray.fromByte(object).resize(size);
	}
}
