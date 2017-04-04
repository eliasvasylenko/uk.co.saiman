package uk.co.saiman.comms;

import java.io.IOException;
import java.nio.channels.ByteChannel;

public interface CommandFunction<I, O> {
	I execute(O output, ByteChannel bytes) throws IOException;
}
