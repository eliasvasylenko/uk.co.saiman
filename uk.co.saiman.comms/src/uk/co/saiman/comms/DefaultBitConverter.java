package uk.co.saiman.comms;

public final class DefaultBitConverter implements BitConverter<DefaultBitConverter> {
	private DefaultBitConverter() {}

	@Override
	public Class<DefaultBitConverter> getType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getDefaultBits() {
		throw new UnsupportedOperationException();
	}

	@Override
	public BitArray toBits(DefaultBitConverter object, int bits) {
		throw new UnsupportedOperationException();
	}

	@Override
	public DefaultBitConverter toObject(BitArray bits) {
		throw new UnsupportedOperationException();
	}

}
