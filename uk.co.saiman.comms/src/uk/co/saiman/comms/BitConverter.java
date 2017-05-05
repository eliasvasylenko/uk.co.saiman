package uk.co.saiman.comms;

public interface BitConverter<T> {
	Class<T> getType();

	int getDefaultBits();

	BitArray toBits(T object, int bits);

	T toObject(BitArray bits);
}
