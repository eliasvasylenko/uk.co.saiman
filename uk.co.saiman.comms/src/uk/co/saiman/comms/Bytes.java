package uk.co.saiman.comms;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface Bytes {
	int count() default 1;

	Class<? extends BitConverter> converter() default BitConverter.class;
}
