/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.msapex.data.
 *
 * uk.co.saiman.msapex.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import uk.co.saiman.data.ContinuousFunction;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.strangeskies.fx.FXUtilities;
import uk.co.strangeskies.mathematics.Range;
import uk.co.strangeskies.mathematics.expression.Expression;

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
	private final ContinuousFunction continuousFunction;
	private final Consumer<Expression<ContinuousFunction>> continuousFunctionObserver;

	/*
	 * Series data
	 */
	private final ObservableList<Data<Number, Number>> data;
	private final Series<Number, Number> series;

	/*
	 * Refresh handling
	 */
	private boolean refresh;
	private AnimationTimer refreshTimer;

	/*
	 * View data
	 */
	private final Range<Double> domain = Range.between(0d, 100d);
	private int resolution = 100;

	/**
	 * Create a mapping from a given {@link ContinuousFunction} to a
	 * {@link Series}.
	 * 
	 * @param continuousFunction
	 *          The backing function
	 */
	public ContinuousFunctionSeries(ContinuousFunction continuousFunction) {
		this.continuousFunction = continuousFunction;
		continuousFunctionObserver = e -> refresh();

		data = FXCollections.observableArrayList();
		series = new Series<>(data);

		continuousFunction.addObserver(continuousFunctionObserver);

		refreshTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				refreshImpl();
			}
		};
		refreshTimer.start();

		refresh();
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
	public ContinuousFunctionSeries(ContinuousFunction continuousFunction, String name) {
		this(continuousFunction);

		series.setName(name);
	}

	@Override
	protected void finalize() throws Throwable {
		continuousFunction.removeObserver(continuousFunctionObserver);
		refreshTimer.stop();
		super.finalize();
	}

	private void refresh() {
		synchronized (refreshTimer) {
			refresh = true;
		}
	}

	private ContinuousFunction getRefreshedContinuousFunction() {
		synchronized (refreshTimer) {
			if (refresh) {
				refresh = false;

				return this.continuousFunction.getValue().copy();
			}
		}

		return null;
	}

	private void refreshImpl() {
		ContinuousFunction continuousFunction = getRefreshedContinuousFunction();

		if (continuousFunction != null) {
			SampledContinuousFunction sampledContinuousFunction = continuousFunction.resample(domain.getFrom(),
					domain.getTo(), resolution);

			FXUtilities.runNow(() -> {
				if (data.size() > sampledContinuousFunction.getDepth()) {
					data.remove(sampledContinuousFunction.getDepth(), data.size());
				}

				for (int i = 0; i < data.size(); i++) {
					data.get(i).setXValue(sampledContinuousFunction.getX(i));
					data.get(i).setYValue(sampledContinuousFunction.getY(i));
				}

				List<Data<Number, Number>> dataTemp = new ArrayList<>(sampledContinuousFunction.getDepth() - data.size());
				for (int i = data.size(); i < sampledContinuousFunction.getDepth(); i++) {
					dataTemp.add(new Data<>(sampledContinuousFunction.getX(i), sampledContinuousFunction.getY(i)));
				}
				data.addAll(dataTemp);
			});
		}
	}

	/**
	 * @return The continuous function backing the series
	 */
	public ContinuousFunction getContinuousFunction() {
		return continuousFunction;
	}

	/**
	 * @return The series providing a view of the continuous function
	 */
	public Series<Number, Number> getSeries() {
		return series;
	}

	/**
	 * Set the interval in the domain of the function we are interested in.
	 * 
	 * @param domain
	 *          An AOI range in the domain of the function
	 */
	public void setDomain(Range<Double> domain) {
		if (!this.domain.equals(domain)) {
			this.domain.set(domain);
			refresh();
		}
	}

	/**
	 * Set the resolution in the domain of the function to which we are
	 * interested.
	 * 
	 * @param resolution
	 *          The number of resolvable units across the domain interval we are
	 *          interested in
	 */
	public void setResolution(int resolution) {
		if (!(this.resolution == resolution)) {
			this.resolution = resolution;
			refresh();
		}
	}
}
