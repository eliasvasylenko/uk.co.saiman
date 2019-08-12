package uk.co.saiman.properties.service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.extender.ExtenderNamespace;

@Requirement(
    namespace = ExtenderNamespace.EXTENDER_NAMESPACE,
    version = PropertiesServiceConstants.EXTENDER_VERSION,
    name = PropertiesServiceConstants.EXTENDER_NAME)
@Retention(RetentionPolicy.CLASS)
public @interface RequirePropertiesServiceExtender {}
