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
public class ContinuumSeries {
	/*
	 * Continuum data
	 */
	private final ContinuousFunction continuum;
	private final Consumer<Expression<ContinuousFunction>> continuumObserver;

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

	public ContinuumSeries(ContinuousFunction continuum) {
		this.continuum = continuum;
		continuumObserver = e -> refresh();

		data = FXCollections.observableArrayList();
		series = new Series<>(data);

		continuum.addObserver(continuumObserver);

		refreshTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				refreshImpl();
			}
		};
		refreshTimer.start();

		refresh();
	}

	public ContinuumSeries(ContinuousFunction continuum, String name) {
		this(continuum);

		series.setName(name);
	}

	@Override
	protected void finalize() throws Throwable {
		continuum.removeObserver(continuumObserver);
		refreshTimer.stop();
		super.finalize();
	}

	public void refresh() {
		synchronized (refreshTimer) {
			refresh = true;
		}
	}

	private ContinuousFunction getRefreshedContinuum() {
		synchronized (refreshTimer) {
			if (refresh) {
				refresh = false;

				return this.continuum.getValue().copy();
			}
		}

		return null;
	}

	private void refreshImpl() {
		ContinuousFunction continuum = getRefreshedContinuum();

		if (continuum != null) {
			SampledContinuousFunction sampledContinuum = continuum.resample(range.getFrom(), range.getTo(), resolution);

			FXUtilities.runNow(() -> {
				if (data.size() > sampledContinuum.getDepth()) {
					data.remove(sampledContinuum.getDepth(), data.size());
				}

				for (int i = 0; i < data.size(); i++) {
					data.get(i).setXValue(sampledContinuum.getXSample(i));
					data.get(i).setYValue(sampledContinuum.getYSample(i));
				}

				List<Data<Number, Number>> dataTemp = new ArrayList<>(sampledContinuum.getDepth() - data.size());
				for (int i = data.size(); i < sampledContinuum.getDepth(); i++) {
					dataTemp.add(new Data<>(sampledContinuum.getXSample(i), sampledContinuum.getYSample(i)));
				}
				data.addAll(dataTemp);
			});
		}
	}

	public ContinuousFunction getContinuum() {
		return continuum;
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
