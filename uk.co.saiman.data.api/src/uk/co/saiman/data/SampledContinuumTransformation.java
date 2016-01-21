package uk.co.saiman.data;

import java.util.function.Function;

import uk.co.strangeskies.mathematics.expression.FunctionExpression;

public class SampledContinuumTransformation<C extends Continuum> extends FunctionExpression<Continuum, Continuum>
		implements SampledContinuumDecorator {
	private final Function<C, SampledContinuum> transformation;

	@SuppressWarnings("unchecked")
	public SampledContinuumTransformation(C dependency, Function<C, SampledContinuum> transformation) {
		super(dependency, c -> transformation.apply((C) c));

		this.transformation = transformation;
	}

	@Override
	public SampledContinuum getComponent() {
		return (SampledContinuum) getValue();
	}

	@Override
	public SampledContinuum copy() {
		C component;

		try {
			getReadLock().lock();
			component = (C) getValue().copy();
		} finally {
			getReadLock().unlock();
		}

		return new SampledContinuumTransformation<>(component, transformation);
	}
}
