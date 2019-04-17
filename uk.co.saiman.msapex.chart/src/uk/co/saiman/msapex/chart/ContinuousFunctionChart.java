/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static java.util.Arrays.asList;
import static uk.co.saiman.mathematics.Interval.bounded;
import static uk.co.saiman.mathematics.Interval.unbounded;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableSet;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.chart.LineChart;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import uk.co.saiman.data.function.ContinuousFunction;
import uk.co.saiman.data.function.Domain;
import uk.co.saiman.mathematics.Interval;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.msapex.annotations.Annotation;
import uk.co.saiman.msapex.annotations.AnnotationLayer;

public class ContinuousFunctionChart<X extends Quantity<X>, Y extends Quantity<Y>>
    extends LineChart<Number, Number> {
  private static final int REPEAT_RATE = 8;
  private static final String CONTINUOUS_FUNCTION_CHART_PSEUDO_CLASS = "continuousFunctionChart";

  public static final double ZOOM_STEP_PERCENTAGE = 20;
  public static final double MAX_ZOOM_STEP = 0.5;
  public static final double PIXEL_ZOOM_DAMP = 50;
  public static final double MOVE_STEP_PERCENTAGE = 10;

  private final Set<ContinuousFunctionSeries<X, Y>> series;

  private Interval<Double> zoom;
  private Interval<Double> domain;
  private Interval<Double> effectiveZoom;

  /*
   * Annotation data
   */
  private AnnotationLayer<X, Y> annotationLayer;

  public ContinuousFunctionChart(Units units, Unit<X> unitX, Unit<Y> unitY) {
    this(createDefaultAxis(units, unitX), createDefaultAxis(units, unitY));
  }

  private static <T extends Quantity<T>> QuantityAxis<T> createDefaultAxis(
      Units units,
      Unit<T> unit) {
    return new QuantityAxis<>(new ScalingTickUnits<>(unit));
  }

  public ContinuousFunctionChart(QuantityAxis<X> xAxis, QuantityAxis<Y> yAxis) {
    super(xAxis, yAxis);
    setAnimated(false);
    setLegendVisible(false);
    idProperty().set(CONTINUOUS_FUNCTION_CHART_PSEUDO_CLASS);
    setFocusTraversable(true);
    setOnScroll(this::onScroll);
    setOnKeyPressed(this::onKeyPressed);

    createTimer();

    series = new HashSet<>();

    annotationLayer = new AnnotationLayer<>(getXAxis().unitProperty(), getYAxis().unitProperty());
    annotationLayer.setManaged(false);
    getChartChildren().add(annotationLayer);

    zoom = unbounded();
    domain = bounded(0d, 0d);
  }

  private AnimationTimer createTimer() {
    AnimationTimer refreshTimer = new AnimationTimer() {
      int count = 0;

      @Override
      public void handle(long now) {
        if (++count >= REPEAT_RATE) {
          count = 0;

          renderData();
        }
      }
    };
    refreshTimer.start();
    return refreshTimer;
  }

  /**
   * @return The domain axis of the chart
   */
  @SuppressWarnings("unchecked")
  @Override
  public QuantityAxis<X> getXAxis() {
    return (QuantityAxis<X>) super.getXAxis();
  }

  /**
   * @return The range axis of the chart
   */
  @SuppressWarnings("unchecked")
  @Override
  public QuantityAxis<Y> getYAxis() {
    return (QuantityAxis<Y>) super.getYAxis();
  }

  public ObservableSet<Annotation<X, Y>> getAnnotations() {
    return annotationLayer.getAnnotations();
  }

  public ContinuousFunctionSeries<X, Y> addSeries() {
    ContinuousFunctionSeries<X, Y> continuousFunctionSeries = new ContinuousFunctionSeries<>();

    series.add(continuousFunctionSeries);
    getData().add(continuousFunctionSeries.getSeries());

    return continuousFunctionSeries;
  }

  public ContinuousFunctionSeries<X, Y> addSeries(ContinuousFunction<X, Y> function) {
    ContinuousFunctionSeries<X, Y> series = addSeries();
    series.setContinuousFunction(function);
    return series;
  }

  void removeSeries(ContinuousFunctionSeries<?, ?> series) {
    if (this.series.remove(series)) {
      getData().remove(series.getSeries());
    }
  }

  private void renderData() {
    boolean seriesChanged = series
        .stream()
        .map(ContinuousFunctionSeries::prepare)
        .reduce((a, b) -> a || b)
        .orElse(false);

    if (seriesChanged) {
      domain = series
          .stream()
          .map(ContinuousFunctionSeries::getLatestPreparedContinuousFunction)
          .filter(Objects::nonNull)
          .map(ContinuousFunction::domain)
          .map(Domain::getInterval)
          .reduce(Interval::getExtendedThrough)
          .orElse(domain);

      updateAxisRange(seriesChanged);
      requestChartLayout();
    }
  }

  @Override
  protected void updateAxisRange() {
    updateAxisRange(false);
  }

  protected void updateAxisRange(boolean seriesChanged) {
    Interval<Double> effectiveZoom = domain.getIntersectionWith(zoom);
    boolean domainChanged = !effectiveZoom.equals(this.effectiveZoom);
    this.effectiveZoom = effectiveZoom;

    if (domainChanged) {
      final QuantityAxis<X> xAxis = getXAxis();

      if (xAxis.isAutoRanging()) {
        xAxis
            .invalidateRange(
                asList(effectiveZoom.getLeftEndpoint(), effectiveZoom.getRightEndpoint()));
      }
    }

    if (domainChanged || seriesChanged) {
      final QuantityAxis<Y> yAxis = getYAxis();

      if (yAxis.isAutoRanging()) {
        getZoomedRange().ifPresent(rangeInterval -> {
          yAxis
              .invalidateRange(
                  asList(rangeInterval.getLeftEndpoint(), rangeInterval.getRightEndpoint()));
        });
      }
    }
  }

  private Optional<Interval<Double>> getZoomedRange() {
    return series
        .stream()
        .map(ContinuousFunctionSeries::getLatestPreparedContinuousFunction)
        .filter(Objects::nonNull)
        .map(ContinuousFunction::range)
        .map(
            r -> r
                .between(effectiveZoom.getLeftEndpoint(), effectiveZoom.getRightEndpoint())
                .getInterval())
        .reduce(Interval::getExtendedThrough);
  }

  void onScroll(ScrollEvent event) {
    double xPosition = getXAxis()
        .getValueForDisplay(getXAxis().sceneToLocal(event.getSceneX(), 0).getX())
        .doubleValue();

    double delta = event.getDeltaY();
    if (delta != 0) {
      double zoomStrength = Math.abs(delta) / PIXEL_ZOOM_DAMP + 1;
      double zoom = 1 + MAX_ZOOM_STEP * (1 - 1 / zoomStrength);

      if (delta < 0) {
        zoom = 1 / zoom;
      }

      zoom(zoom, xPosition);
    }
    event.consume();
  }

  @Override
  protected void dataItemAdded(
      Series<Number, Number> series,
      int itemIndex,
      Data<Number, Number> item) {}

  void onKeyPressed(KeyEvent event) {
    boolean consume = true;

    switch (event.getCode()) {
    case UP:
      zoomIn();
      break;
    case DOWN:
      zoomOut();
      break;
    case LEFT:
      moveZoomLeft();
      break;
    case RIGHT:
      moveZoomRight();
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
   * Zoom in the view in the domain by {@value #ZOOM_STEP_PERCENTAGE}%.
   */
  public void zoomIn() {
    zoom(1 + (ZOOM_STEP_PERCENTAGE / 100d));
  }

  /**
   * Zoom out the view in the domain by {@value #ZOOM_STEP_PERCENTAGE}%.
   */
  public void zoomOut() {
    zoom(1 / (1 + (ZOOM_STEP_PERCENTAGE / 100d)));
  }

  /**
   * Zoom in the view in the domain by the given factor.
   * 
   * @param zoomAmount The amount to zoom in, as a multiplier of the size of a
   *                   chart beneath a fixed viewport
   */
  public void zoom(double zoomAmount) {
    zoom(zoomAmount, (effectiveZoom.getRightEndpoint() - effectiveZoom.getLeftEndpoint()) / 2);
  }

  /**
   * Zoom in the view in the domain by the given factor, about the given centre.
   * 
   * @param zoomAmount The amount to zoom in, as a multiplier of the size of a
   *                   chart beneath a fixed viewport
   * @param centre     The focus of the zoom in the domain
   */
  public void zoom(double zoomAmount, double centre) {
    double zoomLeft = centre - effectiveZoom.getLeftEndpoint();
    double zoomRight = effectiveZoom.getRightEndpoint() - centre;

    double scaleFactor = (1 / zoomAmount - 1) / 2;

    setZoom(
        effectiveZoom.getLeftEndpoint() - scaleFactor * zoomLeft,
        effectiveZoom.getRightEndpoint() + scaleFactor * zoomRight);
  }

  /**
   * Reset the zoom over the domain to contain the entire range of all member
   * functions.
   */
  public void resetZoom() {
    if (!zoom.isUnbounded()) {
      zoom = unbounded();
      updateAxisRange();
    }
  }

  /**
   * Move the view over the underlying charts to the left by
   * {@value #MOVE_STEP_PERCENTAGE}%.
   */
  public void moveZoomLeft() {
    moveZoom(-MOVE_STEP_PERCENTAGE);
  }

  /**
   * Move the view over the underlying charts to the right by
   * {@value #MOVE_STEP_PERCENTAGE}%.
   */
  public void moveZoomRight() {
    moveZoom(MOVE_STEP_PERCENTAGE);
  }

  /**
   * Move the view over the underlying charts through the domain by a given
   * percentage.
   * 
   * @param percentage A percentage of the full width of the view area by which to
   *                   move the chart
   */
  public void moveZoom(double percentage) {
    if (!zoom.isUnbounded()) {
      double amount = (effectiveZoom.getRightEndpoint() - effectiveZoom.getLeftEndpoint())
          * percentage
          / 100;

      setZoom(effectiveZoom.getLeftEndpoint() + amount, effectiveZoom.getRightEndpoint() + amount);
    }
  }

  /**
   * Move the view of the domain to contain exactly the interval between the given
   * values.
   * 
   * @param from The leftmost value in the domain to show in the view
   * @param to   The rightmost value in the domain to show in the view
   */
  public void setZoom(double from, double to) {
    if ((to - from) > (domain.getRightEndpoint() - domain.getLeftEndpoint())) {
      resetZoom();
    } else {
      if (to > domain.getRightEndpoint()) {
        from -= to - domain.getRightEndpoint();
        to = domain.getRightEndpoint();
      } else if (from < domain.getLeftEndpoint()) {
        to += domain.getLeftEndpoint() - from;
        from = domain.getLeftEndpoint();
      }

      zoom = bounded(from, to);
      updateAxisRange();
    }
  }

  @Override
  protected void layoutPlotChildren() {
    series.stream().forEach(s -> s.render(getXAxis().getRange(), getYAxis().getRange()));

    super.layoutPlotChildren();

    Bounds bounds = getPlotArea();

    annotationLayer.setMeasurementBounds(getMeasurementArea());
    annotationLayer
        .resizeRelocate(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
  }

  private Bounds getMeasurementArea() {
    double xLower = getXAxis().getLowerBound();
    double xUpper = getXAxis().getUpperBound();

    double yLower = getYAxis().getLowerBound();
    double yUpper = getYAxis().getUpperBound();

    if (yUpper < yLower)
      return new BoundingBox(xLower, yLower, xUpper - xLower, yUpper - yLower);
    else
      return new BoundingBox(xLower, yUpper, xUpper - xLower, yLower - yUpper);
  }

  private Bounds getPlotArea() {
    double xLower = getXAxis().getDisplayPosition(getXAxis().getLowerBound());
    double xUpper = getXAxis().getDisplayPosition(getXAxis().getUpperBound());

    double yLower = getYAxis().getDisplayPosition(getYAxis().getLowerBound());
    double yUpper = getYAxis().getDisplayPosition(getYAxis().getUpperBound());

    double x;
    double y;
    double width;
    double height;
    if (xLower < xUpper) {
      x = xLower;
      width = xUpper - xLower;
    } else if (xLower > xUpper) {
      x = xUpper;
      width = xLower - xUpper;
    } else {
      x = xLower;
      width = 1;
    }
    if (yLower < yUpper) {
      y = yLower;
      height = yUpper - yLower;
    } else if (yLower > yUpper) {
      y = yUpper;
      height = yLower - yUpper;
    } else {
      y = yLower;
      height = 1;
    }

    x = getXAxis().localToParent(x, 0).getX();
    y = getYAxis().localToParent(y, 0).getY();

    return new BoundingBox(x, y, width, height);
  }
}
