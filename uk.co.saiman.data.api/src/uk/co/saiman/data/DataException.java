package uk.co.saiman.data;

import static uk.co.strangeskies.text.properties.PropertyLoader.getDefaultProperties;

import java.util.function.Function;

import uk.co.strangeskies.text.properties.Localized;
import uk.co.strangeskies.text.properties.LocalizedRuntimeException;

public class DataException extends LocalizedRuntimeException {
	public DataException(Function<DataProperties, Localized<String>> message) {
		this(message, null);
	}

	public DataException(Function<DataProperties, Localized<String>> message, Throwable cause) {
		super(message.apply(getDefaultProperties(DataProperties.class)), cause);
	}

	private static final long serialVersionUID = 1L;
}
