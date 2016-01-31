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
 * 
 * @author Elias N Vasylenko
 *
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
	private final Range<Double> range = Range.between(0d, 100d);
	private int resolution = 100;

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

	public void refresh() {
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
			SampledContinuousFunction sampledContinuousFunction = continuousFunction.resample(range.getFrom(), range.getTo(),
					resolution);

			FXUtilities.runNow(() -> {
				if (data.size() > sampledContinuousFunction.getDepth()) {
					data.remove(sampledContinuousFunction.getDepth(), data.size());
				}

				for (int i = 0; i < data.size(); i++) {
					data.get(i).setXValue(sampledContinuousFunction.getXSample(i));
					data.get(i).setYValue(sampledContinuousFunction.getYSample(i));
				}

				List<Data<Number, Number>> dataTemp = new ArrayList<>(sampledContinuousFunction.getDepth() - data.size());
				for (int i = data.size(); i < sampledContinuousFunction.getDepth(); i++) {
					dataTemp.add(new Data<>(sampledContinuousFunction.getXSample(i), sampledContinuousFunction.getYSample(i)));
				}
				data.addAll(dataTemp);
			});
		}
	}

	public ContinuousFunction getContinuousFunction() {
		return continuousFunction;
	}

	public Series<Number, Number> getSeries() {
		return series;
	}

	public void setRange(Range<Double> range) {
		if (!this.range.equals(range)) {
			this.range.set(range);
			refresh();
		}
	}

	public void setResolution(int resolution) {
		if (!(this.resolution == resolution)) {
			this.resolution = resolution;
			refresh();
		}
	}
}
