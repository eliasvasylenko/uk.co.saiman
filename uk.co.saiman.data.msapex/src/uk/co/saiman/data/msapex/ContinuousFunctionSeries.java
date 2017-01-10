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

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import uk.co.saiman.data.ContinuousFunction;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.strangeskies.mathematics.Range;
import uk.co.strangeskies.mathematics.expression.Expression;
import uk.co.strangeskies.utilities.Observer;

/**
 * A mapping from a {@link ContinuousFunction} to a {@link Series}. The series
 * is backed by the function in that changes in the function will be reflected
 * in the series. The series is only updated as frequently as necessary to
 * achieve a real-time image.
 * <p>
 * The series is only created over a given view range, and to a given
 * resolution, to avoid unnecessary computation.
 * 
 * @author Elias N Vasylenko
 */
public class ContinuousFunctionSeries {
	/*
	 * Continuous function data
	 */
	private final ContinuousFunction<?, ?> continuousFunction;
	private final Observer<Expression<? extends ContinuousFunction<?, ?>>> continuousFunctionObserver;
	private ContinuousFunction<?, ?> latestRenderedContinuousFunction = null;

	/*
	 * Series data
	 */
	private final ObservableList<Data<Number, Number>> data;
	private final Series<Number, Number> series;

	/*
	 * Refresh handling
	 */
	private Object mutex;
	private boolean dirty;

	/**
	 * Create a mapping from a given {@link ContinuousFunction} to a
	 * {@link Series}.
	 * 
	 * @param continuousFunction
	 *          The backing function
	 */
	public ContinuousFunctionSeries(ContinuousFunction<?, ?> continuousFunction) {
		this.continuousFunction = continuousFunction;

		data = FXCollections.observableArrayList();
		series = new Series<>(data);

		mutex = new Object();
		makeDirty();
		refresh();
		makeDirty();

		continuousFunctionObserver = e -> makeDirty();
		continuousFunction.addWeakObserver(this, s -> s.continuousFunctionObserver);
	}

	/**
	 * Create a mapping from a given {@link ContinuousFunction} to a
	 * {@link Series}.
	 * 
	 * @param continuousFunction
	 *          The backing function
	 * @param name
	 *          The name of the series
	 */
	public ContinuousFunctionSeries(ContinuousFunction<?, ?> continuousFunction, String name) {
		this(continuousFunction);

		series.setName(name);
	}

	/**
	 * Flag the series as out of date with respect to the rendering environment or
	 * continuous function state.
	 */
	public void makeDirty() {
		synchronized (mutex) {
			dirty = true;
		}
	}

	/**
	 * Clear the dirty flag, if present, and ensure the
	 * {@link #latestRenderedContinuousFunction latest continuous function for
	 * rendering} is up to date.
	 * 
	 * @return true if the latest continuous function for rendering has changed,
	 *         false otherwise
	 */
	public boolean refresh() {
		boolean dirtied = false;
		synchronized (mutex) {
			if (dirty) {
				dirtied = true;
				dirty = false;

				latestRenderedContinuousFunction = continuousFunction.decoupleValue();
			}
		}

		return dirtied;
	}

	/**
	 * Render the {@link #latestRenderedContinuousFunction latest continuous
	 * function} into the {@link #getSeries() series} for the given range and
	 * resolution.
	 * 
	 * @param domain
	 *          the range to render through in the domain
	 * @param resolution
	 *          the resolution to render at in the domain
	 */
	public void render(Range<Double> domain, int resolution) {
		SampledContinuousFunction<?, ?> sampledContinuousFunction = latestRenderedContinuousFunction
				.resample(domain.getFrom(), domain.getTo(), resolution);

		if (data.size() > sampledContinuousFunction.getDepth()) {
			data.remove(sampledContinuousFunction.getDepth(), data.size());
		}

		for (int i = 0; i < data.size(); i++) {
			data.get(i).setXValue(sampledContinuousFunction.getX(i));
			data.get(i).setYValue(sampledContinuousFunction.getY(i));
		}

		int remainingData = sampledContinuousFunction.getDepth() - data.size();
		if (remainingData > 0) {
			List<Data<Number, Number>> dataTemp = new ArrayList<>(remainingData);
			for (int i = data.size(); i < sampledContinuousFunction.getDepth(); i++) {
				dataTemp.add(new Data<>(sampledContinuousFunction.getX(i), sampledContinuousFunction.getY(i)));
			}
			data.addAll(dataTemp);
		}
	}

	/**
	 * @return the continuous function backing the series
	 */
	public ContinuousFunction<?, ?> getBackingContinuousFunction() {
		return continuousFunction;
	}

	/**
	 * @return the latest continuous function prepared for rendering
	 */
	public ContinuousFunction<?, ?> getLatestRenderedContinuousFunction() {
		return latestRenderedContinuousFunction;
	}

	/**
	 * @return The series providing a view of the continuous function
	 */
	public Series<Number, Number> getSeries() {
		return series;
	}
}
