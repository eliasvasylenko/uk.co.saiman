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

import static uk.co.strangeskies.fx.FxUtilities.wrap;
import static uk.co.strangeskies.mathematics.Range.between;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.measure.Unit;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import uk.co.saiman.data.ContinuousFunction;
import uk.co.saiman.data.DataException;
import uk.co.saiman.data.DataProperties;
import uk.co.saiman.measurement.Units;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.mathematics.Range;
import uk.co.strangeskies.reflection.token.TypeToken;

/**
 * FXML controller for an annotatable data chart over a
 * {@link ContinuousFunction}.
 * 
 * @author Elias N Vasylenko
 */
public class ContinuousFunctionChartController {
	@FXML
	private Pane msapexDataChart;

	@FXML
	private Label noChartDataLabel;

	/*
	 * Chart
	 */
	@FXML
	private LineChart<Number, Number> lineChart;

	@FXML
	private HBox extraAxesContainer;

	private ObservableSet<ContinuousFunction<?, ?>> continuousFunctions;
	private final Map<ContinuousFunction<?, ?>, ContinuousFunctionSeries> series;

	private boolean zoomed;
	private final Range<Double> domain = between(0d, 100d);
	private final Map<Unit<?>, RangeGroup> rangeGroups;
	private final List<NumberAxis> extraYAxes;
	private static final double ZOOM_STEP_PERCENTAGE = 20;
	private static final double MAX_ZOOM_STEP = 0.5;
	private static final double PIXEL_ZOOM_DAMP = 50;
	private static final double MOVE_STEP_PERCENTAGE = 10;

	private static final int REPEAT_RATE = 4;
	private final AnimationTimer refreshTimer;

	/*
	 * Annotations
	 */
	@FXML
	private AnchorPane annotationPane;

	private ObservableSet<ChartAnnotation<?>> annotations;

	private Map<TypeToken<?>, AnnotationHandler<?>> annotationHandlers;
	private Map<ChartAnnotation<?>, Node> annotationNodes;

	@Inject
	Units units;
	@Inject
	@Localize
	DataProperties properties;

	public ContinuousFunctionChartController() {
		continuousFunctions = FXCollections.observableSet(new LinkedHashSet<>());
		series = new HashMap<>();

		rangeGroups = new HashMap<>();
		extraYAxes = new ArrayList<>();

		annotations = FXCollections.observableSet(new LinkedHashSet<>());
		annotationHandlers = new HashMap<>();
		annotationNodes = new HashMap<>();

		refreshTimer = new AnimationTimer() {
			int count = 0;

			@Override
			public void handle(long now) {
				if (++count >= REPEAT_RATE) {
					count = 0;

					triggerRefresh();
				}
			}
		};
		refreshTimer.start();
	}

	@FXML
	void initialize() {
		getXAxis().upperBoundProperty().addListener(num -> updateAnnotations());
		getXAxis().scaleProperty().addListener(num -> updateAnnotations());

		getYAxis().upperBoundProperty().addListener(num -> updateAnnotations());
		getYAxis().scaleProperty().addListener(num -> updateAnnotations());

		annotations.addListener(this::annotationsChanged);
		continuousFunctions.addListener(this::continuousFunctionsChanged);

		getXAxis().scaleProperty().addListener(num -> updateAxis(getXAxis()));
		getYAxis().scaleProperty().addListener(num -> updateAxis(getYAxis()));

		resetZoomDomain();
		updateAxisUnits();
		updateAnnotations();

		noChartDataLabel.textProperty().bind(wrap(properties.noChartData()));
	}

	@Override
	protected void finalize() throws Throwable {
		refreshTimer.stop();
		super.finalize();
	}

	/**
	 * @return The root node of the chart
	 */
	public Pane getRoot() {
		return msapexDataChart;
	}

	/**
	 * @return The domain axis of the chart
	 */
	public NumberAxis getXAxis() {
		return (NumberAxis) lineChart.getXAxis();
	}

	/**
	 * @return The range axis of the chart
	 */
	public NumberAxis getYAxis() {
		return (NumberAxis) lineChart.getYAxis();
	}

	/**
	 * @param title
	 *          The title of the chart
	 */
	public void setTitle(String title) {
		lineChart.setTitle(title);
	}

	/**
	 * @return The backing functions of the chart
	 */
	public ObservableSet<ContinuousFunction<?, ?>> getContinuousFunctions() {
		return continuousFunctions;
	}

	/**
	 * @return The annotations on the chart
	 */
	public ObservableSet<ChartAnnotation<?>> getAnnotations() {
		return annotations;
	}

	private Stream<ContinuousFunctionSeries> series() {
		return series.values().stream();
	}

	private void makeDirty() {
		series().forEach(ContinuousFunctionSeries::makeDirty);
	}

	/**
	 * Request the chart receive UI focus
	 * 
	 * @param event
	 *          the mouse press event
	 */
	public void onMousePressed(MouseEvent event) {
		msapexDataChart.requestFocus();
		event.consume();
	}

	@FXML
	void onScroll(ScrollEvent event) {
		double xPosition = getXAxis()
				.getValueForDisplay(event.getSceneX() - getXAxis().localToScene(0, 0).getX())
				.doubleValue();

		double delta = event.getDeltaY();
		if (delta != 0) {
			double zoomStrength = Math.abs(delta) / PIXEL_ZOOM_DAMP + 1;
			double zoom = 1 + MAX_ZOOM_STEP * (1 - 1 / zoomStrength);

			if (delta < 0) {
				zoom = 1 / zoom;
			}

			zoomDomain(zoom, xPosition);
		}
		event.consume();
	}

	@FXML
	void onKeyPressed(KeyEvent event) {
		boolean consume = true;

		switch (event.getCode()) {
		case UP:
			zoomInDomain();
			break;
		case DOWN:
			zoomOutDomain();
			break;
		case LEFT:
			moveLeftDomain();
			break;
		case RIGHT:
			moveRightDomain();
			break;
		default:
			consume = false;
			break;
		}

		if (consume) {
			event.consume();
		}
	}

	/**
	 * @return The current view range in the domain
	 */
	public Range<Double> getDomain() {
		synchronized (domain) {
			return domain;
		}
	}

	private Range<Double> getMaxZoom(
			Function<ContinuousFunction<?, ?>, Range<Double>> continuousFunctionRange) {
		synchronized (domain) {
			return series()
					.map(ContinuousFunctionSeries::getLatestRenderedContinuousFunction)
					.map(continuousFunctionRange)
					.reduce(Range::getExtendedThrough)
					.orElse(Range.between(0d, 100d));
		}
	}

	/**
	 * Zoom in the view in the domain by {@value #ZOOM_STEP_PERCENTAGE}%.
	 */
	public void zoomInDomain() {
		synchronized (domain) {
			zoomDomain(1 + (ZOOM_STEP_PERCENTAGE / 100d));
		}
	}

	/**
	 * Zoom out the view in the domain by {@value #ZOOM_STEP_PERCENTAGE}%.
	 */
	public void zoomOutDomain() {
		synchronized (domain) {
			zoomDomain(1 / (1 + (ZOOM_STEP_PERCENTAGE / 100d)));
		}
	}

	/**
	 * Zoom in the view in the domain by the given factor.
	 * 
	 * @param zoomAmount
	 *          The amount to zoom in, as a multiplier of the size of a chart
	 *          beneath a fixed viewport
	 */
	public void zoomDomain(double zoomAmount) {
		synchronized (domain) {
			zoomDomain(zoomAmount, (getDomain().getTo() + getDomain().getFrom()) / 2);
		}
	}

	/**
	 * Zoom in the view in the domain by the given factor, about the given centre.
	 * 
	 * @param zoomAmount
	 *          The amount to zoom in, as a multiplier of the size of a chart
	 *          beneath a fixed viewport
	 * @param centre
	 *          The focus of the zoom in the domain
	 */
	public void zoomDomain(double zoomAmount, double centre) {
		synchronized (domain) {
			double zoomLeft = centre - getDomain().getFrom();
			double zoomRight = getDomain().getTo() - centre;

			double scaleFactor = (1 / zoomAmount - 1) / 2;

			setDomain(
					getDomain().getFrom() - scaleFactor * zoomLeft,
					getDomain().getTo() + scaleFactor * zoomRight);
		}
	}

	/**
	 * Reset the zoom over the domain to contain the entire range of all member
	 * functions.
	 */
	public void resetZoomDomain() {
		resetZoomDomainImpl();
	}

	protected boolean resetZoomDomainImpl() {
		boolean changed = false;

		synchronized (domain) {
			Range<Double> maxZoom = getMaxZoom(f -> f.domain().getExtent());

			changed = !maxZoom.equals(domain);
			if (changed) {
				setDomainImpl(maxZoom.getFrom(), maxZoom.getTo());
			}

			zoomed = false;
		}

		return changed;
	}

	/**
	 * Move the view over the underlying charts to the left by
	 * {@value #MOVE_STEP_PERCENTAGE}%.
	 */
	public void moveLeftDomain() {
		moveDomain(-MOVE_STEP_PERCENTAGE);
	}

	/**
	 * Move the view over the underlying charts to the right by
	 * {@value #MOVE_STEP_PERCENTAGE}%.
	 */
	public void moveRightDomain() {
		moveDomain(MOVE_STEP_PERCENTAGE);
	}

	/**
	 * Move the view over the underlying charts through the domain by a given
	 * percentage.
	 * 
	 * @param percentage
	 *          A percentage of the full width of the view area by which to move
	 *          the chart
	 */
	public void moveDomain(double percentage) {
		synchronized (domain) {
			if (zoomed) {
				Range<Double> range = getDomain();

				double amount = (range.getTo() - range.getFrom()) * percentage / 100;

				setDomain(range.getFrom() + amount, range.getTo() + amount);
			}
		}
	}

	/**
	 * Move the view of the domain to contain exactly the interval between the
	 * given values.
	 * 
	 * @param from
	 *          The leftmost value in the domain to show in the view
	 * @param to
	 *          The rightmost value in the domain to show in the view
	 */
	public void setDomain(double from, double to) {
		synchronized (this.domain) {
			Range<Double> maxZoom = getMaxZoom(f -> f.domain().getExtent());

			if ((to - from) > (maxZoom.getTo() - maxZoom.getFrom())) {
				resetZoomDomain();
			} else {
				if (to > maxZoom.getTo()) {
					from -= to - maxZoom.getTo();
					to = maxZoom.getTo();
				} else if (from < maxZoom.getFrom()) {
					to += maxZoom.getFrom() - from;
					from = maxZoom.getFrom();
				}

				setDomainImpl(from, to);
			}
		}
	}

	private void setDomainImpl(double from, double to) {
		synchronized (this.domain) {
			this.domain.setFrom(from);
			this.domain.setTo(to);
			getXAxis().setLowerBound(from);
			getXAxis().setUpperBound(to);

			zoomed = true;
			updateZoomRange(true);

			makeDirty();
		}
	}

	public Stream<NumberAxis> getYAxes() {
		return Stream.concat(Stream.of(getYAxis()), extraYAxes.stream());
	}

	public RangeGroup getRangeGroup(Unit<?> unit) {
		return rangeGroups.get(unit);
	}

	public Stream<RangeGroup> getRangeGroups() {
		return rangeGroups.values().stream();
	}

	private void updateZoomRange(boolean reset) {
		getRangeGroups().forEach(g -> g.updateRangeZoom(domain, reset));
	}

	private void annotationsChanged(Change<? extends ChartAnnotation<?>> c) {
		if (c.wasAdded()) {
			ChartAnnotation<?> annotation = c.getElementAdded();

			@SuppressWarnings("unchecked")
			AnnotationHandler<Object> handler = (AnnotationHandler<Object>) annotationHandlers
					.get(annotation.getDataType());
			Node annotationNode = handler.handle(annotation.getData());

			annotationNodes.put(annotation, annotationNode);

			annotationPane.getChildren().add(annotationNode);
		} else if (c.wasRemoved()) {
			annotationPane.getChildren().remove(annotationNodes.get(c.getElementRemoved()));
		}

		updateAnnotations();
	}

	private void continuousFunctionsChanged(Change<? extends ContinuousFunction<?, ?>> d) {
		if (d.wasAdded()) {
			ContinuousFunction<?, ?> continuousFunction = d.getElementAdded();
			ContinuousFunctionSeries continuousFunctionSeries = new ContinuousFunctionSeries(
					continuousFunction);

			series.put(continuousFunction, continuousFunctionSeries);
			lineChart.getData().add(continuousFunctionSeries.getSeries());
		} else if (d.wasRemoved()) {
			ContinuousFunction<?, ?> continuousFunction = d.getElementRemoved();
			ContinuousFunctionSeries continuousFunctionSeries;

			continuousFunctionSeries = series.remove(continuousFunction);
			lineChart.getData().remove(continuousFunctionSeries.getSeries());

			continuousFunction.removeOwnedObserver(this);

			if (series.isEmpty()) {
				zoomed = false;
			}
		}

		noChartDataLabel.setVisible(lineChart.getData().isEmpty());

		updateAnnotations();
	}

	private void triggerRefresh() {
		synchronized (this.domain) {
			boolean refreshed = series()
					.map(ContinuousFunctionSeries::refresh)
					.reduce(false, (a, b) -> a || b);

			if (refreshed) {
				if (!zoomed) {
					resetZoomDomainImpl();
				}

				updateAxisUnits();
				updateZoomRange(false);

				series().forEach(s -> s.render(domain, getResolution(getXAxis())));
			}
		}
	}

	private int getResolution(NumberAxis axis) {
		int xFrom = (int) axis.getDisplayPosition(domain.getFrom());
		int xTo = (int) axis.getDisplayPosition(domain.getTo());
		return xTo - xFrom;
	}

	private void updateAxisUnits() {
		if (getContinuousFunctions().isEmpty()) {
			extraAxesContainer.getChildren().clear();
		} else {
			List<Node> extraAxes = extraAxesContainer.getChildren();
			extraAxesContainer.getChildren().clear();

			Unit<?> xUnit = null;
			Set<Unit<?>> yUnits = new LinkedHashSet<>();

			for (RangeGroup group : rangeGroups.values()) {
				group.clearContinuousFunctions();
			}

			for (ContinuousFunction<?, ?> function : getContinuousFunctions()) {
				Unit<?> functionXUnit = function.domain().getUnit();
				Unit<?> functionYUnit = function.range().getUnit();

				if (xUnit == null) {
					xUnit = functionXUnit;
				} else {
					if (!xUnit.equals(functionXUnit)) {
						Unit<?> xUnitFinal = xUnit;
						throw new DataException(properties.incompatibleDomainUnits(xUnitFinal, functionXUnit));
					}
				}
				yUnits.add(functionYUnit);

				RangeGroup group = rangeGroups
						.computeIfAbsent(functionYUnit, u -> new RangeGroup(units, functionYUnit));

				group.addContinuousFunction(series.get(function));
			}
			getXAxis().setLabel(units.formatUnit(xUnit));

			rangeGroups.keySet().retainAll(yUnits);

			Iterator<Unit<?>> yUnitIterator = yUnits.iterator();
			rangeGroups.get(yUnitIterator.next()).setAxis(getYAxis());

			// yUnitIterator = yUnits.iterator();

			Iterator<Node> extraAxesIterator = extraAxes.iterator();
			while (yUnitIterator.hasNext()) {
				NumberAxis extraAxis;
				if (extraAxesIterator.hasNext()) {
					extraAxis = (NumberAxis) extraAxesIterator.next();
				} else {
					extraAxis = new NumberAxis();
				}

				rangeGroups.get(yUnitIterator.next()).setAxis(extraAxis);

				extraAxesContainer.getChildren().add(extraAxis);
			}
		}
	}

	private void updateAxis(NumberAxis axis) {
		int resolutionEstimate = getResolution(axis);

		/*
		 * TODO set graph units
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * from now on all members of the graph must use the same units. it's just
		 * simpler that way. have conversion functions between continuous functions
		 * to facilitate this.
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 */

	}

	private void updateAnnotations() {
		for (ChartAnnotation<?> annotation : new ArrayList<>(annotations)) {
			Node node = annotationNodes.get(annotation);

			double x = getXAxis()
					.localToParent(getXAxis().getDisplayPosition(annotation.getX()), 0)
					.getX() + lineChart.getPadding().getLeft();
			double y = getYAxis()
					.localToParent(0, getYAxis().getDisplayPosition(annotation.getY()))
					.getY() + lineChart.getPadding().getTop();

			node.autosize();
			node.setLayoutX(x);
			node.setLayoutY(y - node.prefHeight(Integer.MAX_VALUE));
		}
	}
}
