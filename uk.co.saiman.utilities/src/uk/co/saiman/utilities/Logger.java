package uk.co.saiman.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;

public interface Logger {
	enum Level {
		DEBUG, INFO, WARNING, ERROR, FATAL
	}

	void log(String message, Level level);

	default void log(Exception exception, Level level) {
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));

		log(exception.getMessage(), level);
		log(stackTrace.toString(), Level.DEBUG);
	}
}
