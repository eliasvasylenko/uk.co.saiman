package uk.co.saiman.data;

import uk.co.strangeskies.mathematics.Range;
import uk.co.strangeskies.mathematics.expression.LockingExpression;

public abstract class LockingSampledContinuousFunction extends LockingExpression<ContinuousFunction, ContinuousFunction>
		implements SampledContinuousFunction {
	@Override
	public Range<Double> getDomain() {
		return read(SampledContinuousFunction.super::getDomain);
	}

	@Override
	public Range<Double> getDomain(int startIndex, int endIndex) {
		return read(() -> SampledContinuousFunction.super.getDomain(startIndex, endIndex));
	}

	@Override
	public double sample(double xPosition) {
		return read(() -> SampledContinuousFunction.super.sample(xPosition));
	}

	@Override
	public int getIndexAbove(double xValue) {
		return read(() -> SampledContinuousFunction.super.getIndexAbove(xValue));
	}

	@Override
	public Range<Double> getRange() {
		return read(SampledContinuousFunction.super::getRange);
	}

	@Override
	public Range<Double> getRangeBetween(double startX, double endX) {
		return read(() -> SampledContinuousFunction.super.getRangeBetween(startX, endX));
	}

	@Override
	public Range<Double> getRangeBetween(int startIndex, int endIndex) {
		return read(() -> SampledContinuousFunction.super.getRangeBetween(startIndex, endIndex));
	}

	@Override
	public SampledContinuousFunction resample(double startX, double endX, int resolvableUnits) {
		return read(() -> SampledContinuousFunction.super.resample(startX, endX, resolvableUnits));
	}
}
