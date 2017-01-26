package uk.co.saiman.data;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.strangeskies.mathematics.expression.ImmutableExpression;

class EmptyContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
		extends ImmutableExpression<ContinuousFunction<UD, UR>> implements SampledContinuousFunction<UD, UR> {
	private final SampledDomain<UD> domain;
	private final SampledRange<UR> range;

	protected EmptyContinuousFunction(Unit<UD> unitDomain, Unit<UR> unitRange) {
		this.domain = new EmptyDomain<>(unitDomain);
		this.range = new EmptyRange<>(this, unitRange);
	}

	@Override
	public SampledDomain<UD> domain() {
		return domain;
	}

	@Override
	public SampledRange<UR> range() {
		return range;
	}

	@Override
	public SampledContinuousFunction<UD, UR> copy() {
		return this;
	}

	@Override
	public ContinuousFunction<UD, UR> getValue() {
		return this;
	}

	@Override
	public SampledContinuousFunction<UD, UR> resample(SampledDomain<UD> resolvableSampleDomain) {
		return this;
	}

	@Override
	public int getDepth() {
		return 1;
	}
}

class EmptyDomain<UD extends Quantity<UD>> implements SampledDomain<UD> {
	private final Unit<UD> unitDomain;

	public EmptyDomain(Unit<UD> unitDomain) {
		this.unitDomain = unitDomain;
	}

	@Override
	public Unit<UD> getUnit() {
		return unitDomain;
	}

	@Override
	public int getDepth() {
		return 1;
	}

	@Override
	public double getSample(int index) {
		if (index != 0)
			throw new ArrayIndexOutOfBoundsException(index);
		return 0;
	}

	@Override
	public int getIndexBelow(double xValue) {
		if (xValue >= 0)
			return 0;
		else
			return -1;
	}
}

class EmptyRange<UR extends Quantity<UR>> extends SampledRange<UR> {
	private final Unit<UR> unitRange;

	public EmptyRange(EmptyContinuousFunction<?, UR> function, Unit<UR> unitRange) {
		super(function);
		this.unitRange = unitRange;
	}

	@Override
	public Unit<UR> getUnit() {
		return unitRange;
	}

	@Override
	public int getDepth() {
		return 1;
	}

	@Override
	public double getSample(int index) {
		if (index != 0)
			throw new ArrayIndexOutOfBoundsException(index);
		return 0;
	}
}
