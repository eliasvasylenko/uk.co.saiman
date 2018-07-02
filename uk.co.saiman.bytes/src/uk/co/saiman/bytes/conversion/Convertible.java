package uk.co.saiman.bytes.conversion;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A meta-annotation to indicate a byte conversion annotation.
 * <p>
 * A byte conversion annotation is an annotation which carries information
 * intended to guide conversion between a type and a sequence of bits. Typically
 * the {@link Target target} of a byte conversion annotation should be
 * {@link ElementType#TYPE type} and {@link ElementType#TYPE_USE type use}, so
 * as to indicate that the annotated type should be handled by a converter which
 * {@link ByteConverterProvider#supportsAnnotation(Class) supports} the
 * annotation.
 * 
 * @author Elias N Vasylenko
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE })
public @interface Convertible {}
