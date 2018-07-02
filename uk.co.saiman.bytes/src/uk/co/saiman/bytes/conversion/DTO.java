package uk.co.saiman.bytes.conversion;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Convertible
@Retention(RUNTIME)
@Target({ TYPE_USE, TYPE })
public @interface DTO {}
