package uk.co.saiman.properties.service;

import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.Constants;

@RequirePropertiesServiceExtender
@Capability(
    namespace = SERVICE_NAMESPACE,
    attribute = {
        Constants.OBJECTCLASS + ":List<String>=${@class}",
        PropertiesServiceConstants.EXTENDER_NAME
            + "="
            + PropertiesServiceConstants.EXTENDER_VERSION })
@Retention(RetentionPolicy.CLASS)
public @interface PropertiesService {
  String location() default "";
}
