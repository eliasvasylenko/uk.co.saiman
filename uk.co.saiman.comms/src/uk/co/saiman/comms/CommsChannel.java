package uk.co.saiman.comms;

import java.nio.channels.ByteChannel;

import uk.co.strangeskies.utilities.ObservableValue;

public interface CommsChannel extends ByteChannel {
	ObservableValue<Integer> availableBytes();

	@Override
	void close();
}
