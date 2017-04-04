package uk.co.saiman.comms;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import uk.co.strangeskies.utilities.Observable;

public interface CommsStream extends Closeable, Observable<ByteBuffer>, WritableByteChannel {}
