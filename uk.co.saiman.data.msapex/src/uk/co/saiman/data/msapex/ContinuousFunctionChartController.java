/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import uk.co.saiman.data.ContinuousFunction;
import uk.co.strangeskies.mathematics.Range;
import uk.co.strangeskies.reflection.TypeToken;

/**
 * FXML controller for an annotatable data chart over a
 * {@link ContinuousFunction}.
 * 
 * @author Elias N Vasylenko
 */
public class ContinuousFunctionChartController {
	@FXML
	private Pane msapexDataChart;

	/*
	 * Chart
	 */
	@FXML
	private LineChart<Number, Number> lineChart;

	private ObservableSet<ContinuousFunction> continuousFunctions = FXCollections.observableSet(new LinkedHashSet<>());
	private final Map<ContinuousFunction, ContinuousFunctionSeries> series = new HashMap<>();

	private boolean zoomed;
	private final Range<Double> domain = Range.between(0d, 100d);
	private final Range<Double> range = Range.between(0d, 100d);
	private static final double ZOOM_STEP_PERCENTAGE = 20;
	private static final double MAX_ZOOM_STEP = 0.5;
	private static final double PIXEL_ZOOM_DAMP = 50;
	private static final double MOVE_STEP_PERCENTAGE = 10;
	private static final Range<Double> RANGE_FIT = Range.between(1.05d, 1.35d);

	/*
	 * Annotations
	 */
	@FXML
	private AnchorPane annotationPane;

	private ObservableSet<ChartAnnotation<?>> annotations = FXCollections.observableSet();

	private Map<TypeToken<?>, AnnotationHandler<?>> annotationHandlers = new HashMap<>();
	private Map<ChartAnnotation<?>, Node> annotationNodes = new HashMap<>();

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
	public ObservableSet<ContinuousFunction> getContinuousFunctions() {
		return continuousFunctions;
	}

	/**
	 * @return The annotations on the chart
	 */
	public ObservableSet<ChartAnnotation<?>> getAnnotations() {
		return annotations;
	}

	@FXML
	void initialize() {
		getXAxis().upperBoundProperty().addListener(num -> updateAnnotations());
		getXAxis().scaleProperty().addListener(num -> updateAnnotations());

		getYAxis().upperBoundProperty().addListener(num -> updateAnnotations());
		getYAxis().scaleProperty().addListener(num -> updateAnnotations());

		annotations.addListener(this::annotationsChanged);
		continuousFunctions.addListener(this::continuousFunctionsChanged);

		getXAxis().scaleProperty().addListener(num -> updateResolutionDomain());

		resetZoomDomain();
		updateResolutionDomain();
		updateAnnotations();
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
		double xPosition = getXAxis().getValueForDisplay(event.getSceneX() - getXAxis().localToScene(0, 0).getX())
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

	private Range<Double> getMaxZoom(Function<ContinuousFunction, Range<Double>> continuousFunctionRange) {
		synchronized (domain) {
			return new ArrayList<>(continuousFunctions).stream().map(continuousFunctionRange)
					.reduce(Range::getExtendedThrough).orElse(Range.between(0d, 100d));
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

			setDomain(getDomain().getFrom() - scaleFactor * zoomLeft, getDomain().getTo() + scaleFactor * zoomRight);
		}
	}

	/**
	 * Reset the zoom over the domain to contain the entire range of all member
	 * functions.
	 */
	public void resetZoomDomain() {
		synchronized (domain) {
			Range<Double> maxZoom = getMaxZoom(ContinuousFunction::getDomain);
			if (!maxZoom.equals(domain)) {
				setDomainImpl(maxZoom.getFrom(), maxZoom.getTo());
			}

			zoomed = false;
		}
	}

	/**
	 * Update the zoom over the codomain to contain the visible range of all
	 * member functions.
	 * 
	 * @param reset
	 *          If true, we reset to the exact needed size, otherwise we keep the
	 *          current size so long as its 'close enough' to within a certain
	 *          range.
	 */
	public void updateZoomRange(boolean reset) {
		synchronized (domain) {
			Range<Double> maxZoom = getMaxZoom(c -> c.getRangeBetween(domain.getFrom(), domain.getTo()));

			if (maxZoom.getFrom() >= 0) {
				range.setFrom(0d);
			} else if (reset || maxZoom.getFrom() * RANGE_FIT.getTo() < range.getFrom()) {
				range.setFrom(maxZoom.getFrom() * RANGE_FIT.getTo());
			} else if (maxZoom.getFrom() * RANGE_FIT.getFrom() > range.getFrom()) {
				range.setFrom(maxZoom.getFrom() * RANGE_FIT.getFrom());
			}

			if (maxZoom.getTo() <= 0) {
				range.setTo(0d);
			} else if (reset || maxZoom.getTo() * RANGE_FIT.getTo() < range.getTo()) {
				range.setTo(maxZoom.getTo() * RANGE_FIT.getTo());
			} else if (maxZoom.getTo() * RANGE_FIT.getFrom() > range.getTo()) {
				range.setTo(maxZoom.getTo() * RANGE_FIT.getFrom());
			}

			getYAxis().setLowerBound(range.getFrom());
			getYAxis().setUpperBound(range.getTo());
		}
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
			Range<Double> maxZoom = getMaxZoom(ContinuousFunction::getDomain);

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

			updateZoomDomain();

			zoomed = true;

			updateZoomRange(true);
		}
	}

	private void updateZoomDomain() {
		synchronized (this.domain) {
			new ArrayList<>(continuousFunctions).stream().map(series::get).forEach(s -> s.setDomain(domain));
		}
	}

	private void updateResolutionDomain() {
		synchronized (this.domain) {
			int from = (int) getXAxis().getDisplayPosition(domain.getFrom());
			int to = (int) getXAxis().getDisplayPosition(domain.getTo());
			new ArrayList<>(continuousFunctions).stream().map(series::get).forEach(s -> s.setResolution(to - from));
		}
	}

	private void annotationsChanged(Change<? extends ChartAnnotation<?>> c) {
		if (c.wasAdded()) {
			ChartAnnotation<?> annotation = c.getElementAdded();

			@SuppressWarnings("unchecked")
			AnnotationHandler<Object> handler = (AnnotationHandler<Object>) annotationHandlers.get(annotation.getDataType());
			Node annotationNode = handler.handle(annotation.getData());

			annotationNodes.put(annotation, annotationNode);

			annotationPane.getChildren().add(annotationNode);
		} else if (c.wasRemoved()) {
			annotationPane.getChildren().remove(annotationNodes.get(c.getElementRemoved()));
		}

		updateAnnotations();
	}

	private void continuousFunctionModified(ContinuousFunction continuousFunction) {
		synchronized (this.domain) {
			if (!zoomed) {
				resetZoomDomain();
			} else {
				updateZoomRange(false);
			}
		}
	}

	private void continuousFunctionsChanged(Change<? extends ContinuousFunction> d) {
		if (d.wasAdded()) {
			ContinuousFunction continuousFunction = d.getElementAdded();

			ContinuousFunctionSeries continuousFunctionSeries = new ContinuousFunctionSeries(continuousFunction);

			series.put(continuousFunction, continuousFunctionSeries);
			lineChart.getData().add(continuousFunctionSeries.getSeries());

			continuousFunction.addWeakObserver(this, o -> c -> o.continuousFunctionModified(c.getValue()));
			continuousFunctionModified(continuousFunction);
		} else if (d.wasRemoved()) {
			ContinuousFunction continuousFunction = d.getElementRemoved();

			lineChart.getData().remove(series.get(continuousFunction).getSeries());

			continuousFunction.removeWeakObserver(this);
		}

		updateAnnotations();
	}

	private void updateAnnotations() {
		for (ChartAnnotation<?> annotation : new ArrayList<>(annotations)) {
			Node node = annotationNodes.get(annotation);

			double x = getXAxis().localToParent(getXAxis().getDisplayPosition(annotation.getX()), 0).getX()
					+ lineChart.getPadding().getLeft();
			double y = getYAxis().localToParent(0, getYAxis().getDisplayPosition(annotation.getY())).getY()
					+ lineChart.getPadding().getTop();

			node.autosize();
			node.setLayoutX(x);
			node.setLayoutY(y - node.prefHeight(Integer.MAX_VALUE));
		}
	}
}
