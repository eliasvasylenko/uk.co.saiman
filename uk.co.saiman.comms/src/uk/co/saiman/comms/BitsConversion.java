package uk.co.saiman.comms;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface BitsConversion {
	int size() default -1;

	@SuppressWarnings("rawtypes")
	Class<? extends BitConverter> converter() default BitConverter.class;
}
