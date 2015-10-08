package uk.co.saiman.processing;

import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

/**
 * A processor instance should be completely idempotent and stateless. Because
 * of this, they may be used asynchronously, and they may be modelled as
 * singletons where appropriate.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 * @param <R>
 */
public interface Processor<T, R> {
	String name();

	R process(T target);

	default TypeToken<T> getTargetType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(Processor.class)
				.resolveTypeArgument(new TypeParameter<T>() {}).infer();
	}

	default TypeToken<R> getResultType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(Processor.class)
				.resolveTypeArgument(new TypeParameter<R>() {}).infer();
	}
}
