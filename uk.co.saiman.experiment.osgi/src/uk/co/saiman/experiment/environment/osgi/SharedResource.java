package uk.co.saiman.experiment.environment.osgi;

import org.osgi.service.component.annotations.ComponentPropertyType;

@ComponentPropertyType
public @interface SharedResource {
  String environment_filter() default "*";
}
