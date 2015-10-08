package uk.co.saiman.processing;

import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

public interface ConfigurableProcessor<T, R, C> {
	Processor<T, R> configure(C configuration);

	default TypeToken<T> getTargetType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(ConfigurableProcessor.class)
				.resolveTypeArgument(new TypeParameter<T>() {}).infer();
	}

	default TypeToken<R> getResultType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(ConfigurableProcessor.class)
				.resolveTypeArgument(new TypeParameter<R>() {}).infer();
	}

	default TypeToken<C> getConfigurationType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(ConfigurableProcessor.class)
				.resolveTypeArgument(new TypeParameter<C>() {}).infer();
	}
}
