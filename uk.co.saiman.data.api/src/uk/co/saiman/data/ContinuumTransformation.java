package uk.co.saiman.data;

import java.util.function.Function;

import uk.co.strangeskies.mathematics.expression.FunctionExpression;

public class ContinuumTransformation<C extends Continuum> extends FunctionExpression<Continuum, Continuum>
		implements ContinuumDecorator {
	private final Function<C, Continuum> transformation;

	@SuppressWarnings("unchecked")
	public ContinuumTransformation(C dependency, Function<C, Continuum> transformation) {
		super(dependency, c -> transformation.apply((C) c));

		this.transformation = transformation;
	}

	@Override
	public Continuum getComponent() {
		return getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Continuum copy() {
		C component;

		try {
			getReadLock().lock();
			component = (C) getValue().copy();
		} finally {
			getReadLock().unlock();
		}

		return new ContinuumTransformation<>(component, transformation);
	}
}
