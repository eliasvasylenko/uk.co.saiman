package uk.co.saiman.facebook.react.impl;

import static uk.co.saiman.facebook.react.ReactConstants.REACT_WEB_RESOURCE_VERSION;

import java.io.InputStream;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.facebook.react.ReactProvider;

@Component
public class ReactProviderImpl implements ReactProvider {
	private static final String ROOT = "META-INF/resources/webjars/";

	@Override
	public InputStream getReactResource(String text) {
		return getClass().getResourceAsStream(ROOT + "react/" + REACT_WEB_RESOURCE_VERSION);
	}

	@Override
	public InputStream getReactDOMResource(String text) {
		return getClass().getResourceAsStream(ROOT + "react-dom/" + REACT_WEB_RESOURCE_VERSION);
	}
}
