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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.measure.Quantity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import uk.co.saiman.data.function.ContinuousFunction;
import uk.co.saiman.data.function.RegularSampledDomain;
import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.data.function.SampledDomain;

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
public class ContinuousFunctionSeries<X extends Quantity<X>, Y extends Quantity<Y>> {
  /*
   * Continuous function data
   */
  private Supplier<? extends ContinuousFunction<X, Y>> latestContinuousFunction;
  private ContinuousFunction<X, Y> lastPreparedContinuousFunction;
  private RenderInformation lastRenderInformation;

  private class RenderInformation {
    private final double lowerBound;
    private final double upperBound;
    private final double pixelLength;

    public RenderInformation(Range<X> domain, Range<Y> range) {
      lowerBound = domain.lowerBound();
      upperBound = domain.upperBound();
      pixelLength = domain.pixelLength();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this)
        return true;

      if (!(obj instanceof ContinuousFunctionSeries<?, ?>.RenderInformation))
        return false;

      ContinuousFunctionSeries<?, ?>.RenderInformation that = (ContinuousFunctionSeries<?, ?>.RenderInformation) obj;

      return this.lowerBound == that.lowerBound
          && this.upperBound == that.upperBound
          && this.pixelLength == that.pixelLength;
    }
  }

  /*
   * Series data
   */
  private final Series<Number, Number> series;

  /**
   * Create a mapping from a given {@link ContinuousFunction} to a {@link Series}.
   */
  public ContinuousFunctionSeries() {
    series = new Series<>(FXCollections.observableArrayList());
  }

  /**
   * Create a mapping from a given {@link ContinuousFunction} to a {@link Series}.
   * 
   * @param name the name of the series
   */
  public ContinuousFunctionSeries(String name) {
    this();
    series.setName(name);
  }

  public synchronized void setContinuousFunction(ContinuousFunction<X, Y> function) {
    latestContinuousFunction = () -> function;
  }

  public synchronized void setContinuousFunction(
      Supplier<? extends ContinuousFunction<X, Y>> function) {
    latestContinuousFunction = function;
  }

  public synchronized boolean prepare() {
    if (latestContinuousFunction != null) {
      try {
        lastPreparedContinuousFunction = latestContinuousFunction.get();
      } catch (Exception e) {
        lastPreparedContinuousFunction = null;
        /*
         * TODO deal properly with the exception. Perhaps display a message on the chart
         */
      }
      latestContinuousFunction = null;

      lastRenderInformation = null;

      return true;
    }
    return false;
  }

  /**
   * Render the {@link #setContinuousFunction(ContinuousFunction) latest set
   * continuous function} into the {@link #getSeries() series} for the given range
   * and resolution.
   * 
   * @param domain the domain to render over
   * @param range  the range to render into
   */
  public void render(Range<X> domain, Range<Y> range) {
    ContinuousFunction<X, Y> lastPreparedContinuousFunction;
    synchronized (this) {
      lastPreparedContinuousFunction = this.lastPreparedContinuousFunction;

      if (lastPreparedContinuousFunction == null) {
        series.setData(FXCollections.observableArrayList());
        return;
      }

      RenderInformation renderInformation = new RenderInformation(domain, range);
      if (lastRenderInformation != null && renderInformation.equals(lastRenderInformation)) {
        return;
      }
      lastRenderInformation = renderInformation;
    }

    SampledContinuousFunction<?, ?> sampledContinuousFunction = resampleLastRendered(
        lastPreparedContinuousFunction,
        domain);

    ObservableList<Data<Number, Number>> data = series.getData();

    if (data.size() > sampledContinuousFunction.getDepth()) {
      data.remove(sampledContinuousFunction.getDepth(), data.size());
    }

    for (int i = 0; i < data.size(); i++) {
      data.get(i).setXValue(sampledContinuousFunction.domain().getSample(i));
      data.get(i).setYValue(sampledContinuousFunction.range().getSample(i));
    }

    int remainingData = sampledContinuousFunction.getDepth() - data.size();
    if (remainingData > 0) {
      List<Data<Number, Number>> dataTemp = new ArrayList<>(remainingData);

      for (int i = data.size(); i < sampledContinuousFunction.getDepth(); i++) {
        dataTemp
            .add(
                new Data<>(
                    sampledContinuousFunction.domain().getSample(i),
                    sampledContinuousFunction.range().getSample(i)));
      }

      data.addAll(dataTemp);
    }
  }

  private SampledContinuousFunction<X, ?> resampleLastRendered(
      ContinuousFunction<X, ?> latestRenderedContinuousFunction,
      Range<X> range) {
    SampledDomain<X> resolvableDomain = new RegularSampledDomain<X>(
        range.tickUnit().unit(),
        (int) range.pixelLength(),
        range.pixelLength() / (range.upperBound() - range.lowerBound()),
        range.lowerBound());

    return latestRenderedContinuousFunction.resample(resolvableDomain);
  }

  /**
   * @return the latest continuous function prepared for rendering
   */
  public ContinuousFunction<X, Y> getLatestPreparedContinuousFunction() {
    return lastPreparedContinuousFunction;
  }

  /**
   * @return The series providing a view of the continuous function
   */
  public Series<Number, Number> getSeries() {
    return series;
  }
}
