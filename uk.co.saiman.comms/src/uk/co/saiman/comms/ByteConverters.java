package uk.co.saiman.comms;

public interface ByteConverters {
	<T> ByteConverter<T> getConverter(Class<T> type);
}
