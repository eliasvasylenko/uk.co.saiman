package uk.co.saiman.msapex.data;

import uk.co.strangeskies.reflection.Reified;
import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

public interface ChartAnnotation<T> extends Reified<ChartAnnotation<T>> {
	T getData();

	TypeToken<T> getDataType();

	@Override
	default TypeToken<ChartAnnotation<T>> getThisType() {
		return new TypeToken<ChartAnnotation<T>>() {}.withTypeArgument(new TypeParameter<T>() {}, getDataType());
	}

	@Override
	default ChartAnnotation<T> getThis() {
		return this;
	}

	double getX();

	double getY();
}
