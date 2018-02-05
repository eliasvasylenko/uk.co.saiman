/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.msapex.chart.
 *
 * uk.co.saiman.msapex.chart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.chart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.chart;

import java.util.HashSet;
import java.util.Set;

import javax.measure.Unit;

import javafx.scene.chart.NumberAxis;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.mathematics.Interval;

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
  private Interval<Double> visibleZoomRange;
  private Interval<Double> totalZoomRange;

  private NumberAxis axis;

  public RangeGroup(Units units, Unit<?> unit) {
    this.units = units;
    this.unit = unit;

    continuousFunctions = new HashSet<>();
    visibleZoomRange = Interval.bounded(0d, 100d);
    totalZoomRange = Interval.bounded(0d, 100d);
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
  public void updateRangeZoom(Interval<Double> domain, boolean reset) {
    // the total range of all the data
    Interval<Double> totalDataRange = continuousFunctions
        .stream()
        .map(ContinuousFunctionSeries::getLatestPreparedContinuousFunction)
        .map(c -> c.range().getInterval())
        .reduce(Interval::getExtendedThrough)
        .orElse(Interval.bounded(0d, 100d));

    // the zoom over all data
    totalZoomRange = updateRangeZoom(reset, totalDataRange, totalZoomRange);

    if (continuousFunctions.stream().allMatch(
        c -> domain.contains(c.getLatestPreparedContinuousFunction().domain().getInterval()))) {

      visibleZoomRange = totalZoomRange;
    } else {
      // the total range of visible data
      Interval<Double> visibleDataRange = continuousFunctions
          .stream()
          .map(ContinuousFunctionSeries::getLatestPreparedContinuousFunction)
          .map(
              c -> c
                  .range()
                  .between(domain.getLeftEndpoint(), domain.getRightEndpoint())
                  .getInterval())
          .reduce(Interval::getExtendedThrough)
          .orElse(Interval.bounded(0d, 100d));

      // the zoom over visible data
      visibleZoomRange = updateRangeZoom(reset, visibleDataRange, visibleZoomRange);
    }

    axis.setLowerBound(visibleZoomRange.getLeftEndpoint() * ZOOM_PADDING);
    axis.setUpperBound(visibleZoomRange.getRightEndpoint() * ZOOM_PADDING);
  }

  public Interval<Double> updateRangeZoom(
      boolean reset,
      Interval<Double> actualDataRange,
      Interval<Double> rangeZoom) {
    if (actualDataRange.getLeftEndpoint() >= 0) {
      rangeZoom = rangeZoom.withLeftBound(0d);

    } else if (rangeZoom.getLeftEndpoint() > actualDataRange.getLeftEndpoint() || reset) {
      rangeZoom = rangeZoom.withLeftBound(actualDataRange.getLeftEndpoint());

    } else if (rangeZoom.getLeftEndpoint() < actualDataRange.getLeftEndpoint() * MAXIMUM_ZOOM) {
      rangeZoom = rangeZoom.withLeftBound(actualDataRange.getLeftEndpoint() * MAXIMUM_ZOOM);
    }

    if (actualDataRange.getRightEndpoint() <= 0) {
      rangeZoom = rangeZoom.withRightBound(0d);

    } else if (rangeZoom.getRightEndpoint() < actualDataRange.getRightEndpoint() || reset) {
      rangeZoom = rangeZoom.withRightBound(actualDataRange.getRightEndpoint());

    } else if (rangeZoom.getRightEndpoint() > actualDataRange.getRightEndpoint() * MAXIMUM_ZOOM) {
      rangeZoom = rangeZoom.withRightBound(actualDataRange.getRightEndpoint() * MAXIMUM_ZOOM);
    }

    return rangeZoom;
  }
}
