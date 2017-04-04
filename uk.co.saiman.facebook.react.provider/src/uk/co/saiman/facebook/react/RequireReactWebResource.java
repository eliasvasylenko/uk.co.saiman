package uk.co.saiman.facebook.react;

import static uk.co.saiman.facebook.react.ReactConstants.REACT_WEB_RESOURCE_NAME;
import static uk.co.saiman.facebook.react.ReactConstants.REACT_WEB_RESOURCE_VERSION;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import aQute.bnd.annotation.headers.RequireCapability;
import osgi.enroute.namespace.WebResourceNamespace;

/**
 * A Web Resource that provides Facebook's React javascript files.
 */
@RequireCapability(
		ns = WebResourceNamespace.NS,
		filter = "(&(" + WebResourceNamespace.NS + "=" + REACT_WEB_RESOURCE_NAME + ")${frange;"
				+ REACT_WEB_RESOURCE_VERSION + "})")
@Retention(RetentionPolicy.CLASS)
public @interface RequireReactWebResource {
	String[] resource() default { "react.js", "react-dom.js" };

	int priority() default 1000;
}
