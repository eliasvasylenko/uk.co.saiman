package uk.co.saiman.comms;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Repeatable(BitsElements.class)
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface Bits {
	int value();

	BitsConversion conversion() default @BitsConversion;
}
