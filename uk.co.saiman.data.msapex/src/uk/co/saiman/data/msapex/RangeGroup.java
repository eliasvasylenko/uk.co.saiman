/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.data.msapex.
 *
 * uk.co.saiman.data.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
		axis.setLabel(units.formatUnit(unit));
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
		Range<Double> totalDataRange = continuousFunctions
				.stream()
				.map(ContinuousFunctionSeries::getLatestRenderedContinuousFunction)
				.map(c -> c.range().getExtent())
				.reduce(Range::getExtendedThrough)
				.orElse(between(0d, 100d));

		// the zoom over all data
		updateRangeZoom(reset, totalDataRange, totalZoomRange);

		if (continuousFunctions
				.stream()
				.allMatch(c -> domain.contains(c.getLatestRenderedContinuousFunction().domain().getExtent()))) {

			visibleZoomRange.set(totalZoomRange);
		} else {
			// the total range of visible data
			Range<Double> visibleDataRange = continuousFunctions
					.stream()
					.map(ContinuousFunctionSeries::getLatestRenderedContinuousFunction)
					.map(c -> c.range().between(domain.getFrom(), domain.getTo()).getExtent())
					.reduce(Range::getExtendedThrough)
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
