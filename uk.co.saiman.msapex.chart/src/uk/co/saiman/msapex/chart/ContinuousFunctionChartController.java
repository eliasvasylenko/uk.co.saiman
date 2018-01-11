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

import static uk.co.saiman.fx.FxUtilities.wrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import uk.co.saiman.data.DataException;
import uk.co.saiman.data.function.ContinuousFunction;
import uk.co.saiman.data.function.FunctionProperties;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.mathematics.Interval;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.reflection.token.TypeToken;

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

  private final Set<ContinuousFunctionSeries> series;

  private boolean zoomed;
  private Interval<Double> domain = Interval.bounded(0d, 100d);
  private boolean dirtyDomain;
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
  FunctionProperties properties;

  public ContinuousFunctionChartController() {
    series = new HashSet<>();

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
   * @return The annotations on the chart
   */
  public ObservableSet<ChartAnnotation<?>> getAnnotations() {
    return annotations;
  }

  private Stream<ContinuousFunctionSeries> series() {
    return series.stream();
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
  public Interval<Double> getDomain() {
    synchronized (domain) {
      return domain;
    }
  }

  private Interval<Double> getMaxZoom(
      Function<ContinuousFunction<?, ?>, Interval<Double>> continuousFunctionRange) {
    synchronized (domain) {
      return series()
          .map(ContinuousFunctionSeries::getLatestPreparedContinuousFunction)
          .filter(Objects::nonNull)
          .map(continuousFunctionRange)
          .reduce(Interval::getExtendedThrough)
          .orElse(Interval.bounded(0d, 100d));
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
      zoomDomain(zoomAmount, (getDomain().getRightEndpoint() + getDomain().getLeftEndpoint()) / 2);
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
      double zoomLeft = centre - getDomain().getLeftEndpoint();
      double zoomRight = getDomain().getRightEndpoint() - centre;

      double scaleFactor = (1 / zoomAmount - 1) / 2;

      setDomain(
          getDomain().getLeftEndpoint() - scaleFactor * zoomLeft,
          getDomain().getRightEndpoint() + scaleFactor * zoomRight);
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
      Interval<Double> maxZoom = getMaxZoom(f -> f.domain().getInterval());

      changed = !maxZoom.equals(domain);
      if (changed) {
        setDomainImpl(maxZoom.getLeftEndpoint(), maxZoom.getRightEndpoint());
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
   *          A percentage of the full width of the view area by which to move the
   *          chart
   */
  public void moveDomain(double percentage) {
    synchronized (domain) {
      if (zoomed) {
        Interval<Double> range = getDomain();

        double amount = (range.getRightEndpoint() - range.getLeftEndpoint()) * percentage / 100;

        setDomain(range.getLeftEndpoint() + amount, range.getRightEndpoint() + amount);
      }
    }
  }

  /**
   * Move the view of the domain to contain exactly the interval between the given
   * values.
   * 
   * @param from
   *          The leftmost value in the domain to show in the view
   * @param to
   *          The rightmost value in the domain to show in the view
   */
  public void setDomain(double from, double to) {
    synchronized (this.domain) {
      Interval<Double> maxZoom = getMaxZoom(f -> f.domain().getInterval());

      if ((to - from) > (maxZoom.getRightEndpoint() - maxZoom.getLeftEndpoint())) {
        resetZoomDomain();
      } else {
        if (to > maxZoom.getRightEndpoint()) {
          from -= to - maxZoom.getRightEndpoint();
          to = maxZoom.getRightEndpoint();
        } else if (from < maxZoom.getLeftEndpoint()) {
          to += maxZoom.getLeftEndpoint() - from;
          from = maxZoom.getLeftEndpoint();
        }

        setDomainImpl(from, to);
      }
    }
  }

  private void setDomainImpl(double from, double to) {
    synchronized (this.domain) {
      domain = Interval.bounded(from, to);
      getXAxis().setLowerBound(from);
      getXAxis().setUpperBound(to);

      zoomed = true;
      updateZoomRange(true);

      dirtyDomain = true;
    }
  }

  private boolean isDomainDirty() {
    synchronized (this.domain) {
      boolean dirty = dirtyDomain;
      dirtyDomain = false;
      return dirty;
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

  public ContinuousFunctionSeries addSeries() {
    ContinuousFunctionSeries continuousFunctionSeries = new ContinuousFunctionSeries(this);

    series.add(continuousFunctionSeries);
    lineChart.getData().add(continuousFunctionSeries.getSeries());

    refreshSeries();

    return continuousFunctionSeries;
  }

  void removeSeries(ContinuousFunctionSeries series) {
    if (this.series.remove(series)) {
      lineChart.getData().remove(series.getSeries());

      refreshSeries();
    }
  }

  private void refreshSeries() {
    if (series.isEmpty()) {
      zoomed = false;
    }

    noChartDataLabel.setVisible(lineChart.getData().isEmpty());

    updateAnnotations();
  }

  private void triggerRefresh() {
    synchronized (this.domain) {
      boolean dirty = isDomainDirty()
          || series().map(ContinuousFunctionSeries::isDirty).reduce(false, (a, b) -> a || b);

      if (dirty) {
        series().forEach(s -> s.prepare());

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
    int xFrom = (int) axis.getDisplayPosition(domain.getLeftEndpoint());
    int xTo = (int) axis.getDisplayPosition(domain.getRightEndpoint());
    return xTo - xFrom;
  }

  private void updateAxisUnits() {
    if (series.isEmpty()) {
      extraAxesContainer.getChildren().clear();
    } else {
      List<Node> extraAxes = extraAxesContainer.getChildren();
      extraAxesContainer.getChildren().clear();

      Unit<?> xUnit = null;
      Set<Unit<?>> yUnits = new LinkedHashSet<>();

      for (RangeGroup group : rangeGroups.values()) {
        group.clearContinuousFunctions();
      }

      for (ContinuousFunctionSeries series : this.series) {
        ContinuousFunction<?, ?> preparedFunction = series.getLatestPreparedContinuousFunction();
        if (preparedFunction != null) {
          Unit<?> functionXUnit = preparedFunction.domain().getUnit();
          Unit<?> functionYUnit = preparedFunction.range().getUnit();

          if (xUnit == null) {
            xUnit = functionXUnit;
          } else {
            if (!xUnit.equals(functionXUnit)) {
              Unit<?> xUnitFinal = xUnit;
              throw new DataException(
                  properties.incompatibleDomainUnits(xUnitFinal, functionXUnit));
            }
          }
          yUnits.add(functionYUnit);

          RangeGroup group = rangeGroups
              .computeIfAbsent(functionYUnit, u -> new RangeGroup(units, functionYUnit));

          group.addContinuousFunction(series);
        }
      }
      if (xUnit == null)
        xUnit = units.count().get();
      getXAxis().setLabel(units.formatUnit(xUnit));

      rangeGroups.keySet().retainAll(yUnits);

      Iterator<Unit<?>> yUnitIterator = yUnits.iterator();
      if (yUnitIterator.hasNext())
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
     * simpler that way. have conversion functions between continuous functions to
     * facilitate this.
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
