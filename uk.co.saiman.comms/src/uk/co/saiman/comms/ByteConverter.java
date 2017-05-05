package uk.co.saiman.comms;

public interface ByteConverter<T> {
	T create();

	T fromBytes(byte[] bytes);

	byte[] toBytes(T object);
}
