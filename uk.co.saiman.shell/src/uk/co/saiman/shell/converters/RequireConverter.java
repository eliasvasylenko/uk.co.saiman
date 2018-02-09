package uk.co.saiman.shell.converters;

import static java.lang.annotation.RetentionPolicy.CLASS;
import static org.osgi.namespace.service.ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE;
import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;
import static org.osgi.resource.Namespace.EFFECTIVE_ACTIVE;

import java.lang.annotation.Retention;

import aQute.bnd.annotation.headers.RequireCapability;

@RequireCapability(
    ns = SERVICE_NAMESPACE,
    filter = "(&("
        + CAPABILITY_OBJECTCLASS_ATTRIBUTE
        + "=org.apache.felix.service.command.Converter)("
        + RequireConverter.TYPE
        + "=${converterType}))",
    effective = EFFECTIVE_ACTIVE)
@Retention(CLASS)
public @interface RequireConverter {
  String TYPE = "type";

  Class<?> converterType();
}
