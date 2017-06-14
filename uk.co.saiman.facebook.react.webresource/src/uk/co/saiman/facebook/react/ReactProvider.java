package uk.co.saiman.facebook.react;

import java.io.InputStream;

public interface ReactProvider {
	InputStream getReactResource(String text);

	InputStream getReactDOMResource(String text);
}
