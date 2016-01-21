package uk.co.saiman.data;

import uk.co.strangeskies.mathematics.expression.DependentExpression;

public class ContinuumExpression extends DependentExpression<Continuum> implements ContinuumDecorator {
	private Continuum component;

	public ContinuumExpression(Continuum component) {
		setComponent(component);
	}

	@Override
	public Continuum getComponent() {
		return component;
	}

	public void setComponent(Continuum component) {
		try {
			getWriteLock().lock();
			this.component = component;
			getDependencies().clear();
			getDependencies().add(component);
		} finally {
			postUpdate();
		}
	}

	@Override
	public ContinuumExpression copy() {
		Continuum component;

		try {
			getReadLock().lock();
			component = getComponent().copy();
		} finally {
			getReadLock().unlock();
		}

		return new ContinuumExpression(component);
	}

	@Override
	protected Continuum evaluate() {
		return this;
	}
}
