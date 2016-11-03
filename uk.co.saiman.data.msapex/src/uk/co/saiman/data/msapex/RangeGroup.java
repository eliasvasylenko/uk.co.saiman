package uk.co.saiman.data.msapex;

import static uk.co.strangeskies.mathematics.Range.between;

import java.util.HashSet;
import java.util.Set;

import javax.measure.Unit;

import javafx.scene.chart.NumberAxis;
import uk.co.saiman.measurement.Units;
import uk.co.strangeskies.mathematics.Range;

/**
 * A range group manages the axis on a graph for a particular unit type, which
 * may apply to a number of continuous functions sharing the same unit type.
 * 
 * @author Elias N Vasylenko
 */
class RangeGroup {
	private static final double MAXIMUM_ZOOM = 1.3;

	private static final double ZOOM_PADDING = 1.1;

	private final Units units;
	private final Unit<?> unit;

	private final Set<ContinuousFunctionSeries> continuousFunctions;
	private final Range<Double> visibleZoomRange;
	private final Range<Double> totalZoomRange;

	private NumberAxis axis;

	public RangeGroup(Units units, Unit<?> unit) {
		this.units = units;
		this.unit = unit;

		continuousFunctions = new HashSet<>();
		visibleZoomRange = between(0d, 100d);
		totalZoomRange = between(0d, 100d);
	}

	public void addContinuousFunction(ContinuousFunctionSeries function) {
		continuousFunctions.add(function);
	}

	public void clearContinuousFunctions() {
		continuousFunctions.clear();
	}

	/**
	 * Sometimes the axis a range group is responsible for may change. We can't
	 * just move the same {@link NumberAxis} instance to a different place in the
	 * graph as the graph's own
	 * {@link ContinuousFunctionChartController#getYAxis() y axis} is fixed.
	 * 
	 * @param axis
	 *          the new axis component this range group should configure
	 */
	public void setAxis(NumberAxis axis) {
		this.axis = axis;
		axis.setLabel(units.format(unit));
	}

	/**
	 * Update the zoom over the codomain to contain the visible range of all
	 * member functions.
	 * 
	 * @param domain
	 *          the currently visible domain whose associated range we wish to
	 *          focus on
	 * @param reset
	 *          If true, we reset to the exact needed size, otherwise we keep the
	 *          current size so long as its 'close enough' to within a certain
	 *          range.
	 */
	public void updateRangeZoom(Range<Double> domain, boolean reset) {
		// the total range of all the data
		Range<Double> totalDataRange = continuousFunctions.stream()
				.map(ContinuousFunctionSeries::getLatestRenderedContinuousFunction).map(c -> c.getRange())
				.reduce(Range::getExtendedThrough).orElse(between(0d, 100d));

		// the zoom over all data
		updateRangeZoom(reset, totalDataRange, totalZoomRange);

		if (continuousFunctions.stream()
				.allMatch(c -> domain.contains(c.getLatestRenderedContinuousFunction().getDomain()))) {

			visibleZoomRange.set(totalZoomRange);
		} else {
			// the total range of visible data
			Range<Double> visibleDataRange = continuousFunctions.stream()
					.map(ContinuousFunctionSeries::getLatestRenderedContinuousFunction)
					.map(c -> c.getRangeBetween(domain.getFrom(), domain.getTo())).reduce(Range::getExtendedThrough)
					.orElse(between(0d, 100d));

			// the zoom over visible data
			updateRangeZoom(reset, visibleDataRange, visibleZoomRange);
		}

		axis.setLowerBound(visibleZoomRange.getFrom() * ZOOM_PADDING);
		axis.setUpperBound(visibleZoomRange.getTo() * ZOOM_PADDING);
	}

	public void updateRangeZoom(boolean reset, Range<Double> actualDataRange, Range<Double> rangeZoom) {
		if (actualDataRange.getFrom() >= 0) {
			rangeZoom.setFrom(0d);

		} else if (rangeZoom.getFrom() > actualDataRange.getFrom() || reset) {
			rangeZoom.setFrom(actualDataRange.getFrom());

		} else if (rangeZoom.getFrom() < actualDataRange.getFrom() * MAXIMUM_ZOOM) {
			rangeZoom.setFrom(actualDataRange.getFrom() * MAXIMUM_ZOOM);
		}

		if (actualDataRange.getTo() <= 0) {
			rangeZoom.setTo(0d);

		} else if (rangeZoom.getTo() < actualDataRange.getTo() || reset) {
			rangeZoom.setTo(actualDataRange.getTo());

		} else if (rangeZoom.getTo() > actualDataRange.getTo() * MAXIMUM_ZOOM) {
			rangeZoom.setTo(actualDataRange.getTo() * MAXIMUM_ZOOM);
		}
	}
}
