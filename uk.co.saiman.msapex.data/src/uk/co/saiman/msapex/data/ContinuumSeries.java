package uk.co.saiman.msapex.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import uk.co.saiman.data.Continuum;
import uk.co.saiman.data.SampledContinuum;
import uk.co.saiman.eclipse.FXUtilities;
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
	private final Continuum continuum;
	private final Consumer<Expression<Continuum>> continuumObserver;

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

	public ContinuumSeries(Continuum continuum) {
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

	public ContinuumSeries(Continuum continuum, String name) {
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

	private Continuum getRefreshedContinuum() {
		synchronized (refreshTimer) {
			if (refresh) {
				refresh = false;

				return this.continuum.getValue().copy();
			}
		}

		return null;
	}

	private void refreshImpl() {
		Continuum continuum = getRefreshedContinuum();

		if (continuum != null) {
			SampledContinuum sampledContinuum = continuum.resample(range.getFrom(), range.getTo(), resolution);

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

	public Continuum getContinuum() {
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
