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
package uk.co.saiman.msapex.chart;

import static uk.co.strangeskies.observable.Observer.onObservation;

import java.util.ArrayList;
import java.util.List;

import javax.measure.Quantity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import uk.co.saiman.data.ContinuousFunction;
import uk.co.saiman.data.RegularSampledDomain;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.data.SampledDomain;
import uk.co.strangeskies.mathematics.Interval;
import uk.co.strangeskies.observable.Observation;
import uk.co.strangeskies.utility.IdentityProperty;
import uk.co.strangeskies.utility.Property;

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
  private final ContinuousFunction<?, ?> sourceContinuousFunction;
  private final Observation observation;
  private ContinuousFunction<?, ?> latestContinuousFunction;
  private boolean dirty;

  /*
   * Series data
   */
  private final ObservableList<Data<Number, Number>> data;
  private final Series<Number, Number> series;

  /**
   * Create a mapping from a given {@link ContinuousFunction} to a
   * {@link Series}.
   * 
   * @param continuousFunction
   *          The backing function
   */
  public ContinuousFunctionSeries(ContinuousFunction<?, ?> continuousFunction) {
    this.sourceContinuousFunction = continuousFunction;

    data = FXCollections.observableArrayList();
    series = new Series<>(data);

    Property<Observation> observation = new IdentityProperty<>();
    continuousFunction
        .changes()
        .weakReference(this)
        .reduceBackpressure((a, b) -> b)
        .then(onObservation(observation::set))
        .observe(m -> m.owner().setValue(m.message()));
    this.observation = observation.get();

    this.observation.requestNext();
  }

  public void dispose() {
    observation.cancel();
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

  public synchronized boolean isDirty() {
    return dirty;
  }

  private synchronized void setValue(ContinuousFunction<?, ?> message) {
    dirty = true;
    latestContinuousFunction = message.copy();
  }

  private synchronized ContinuousFunction<?, ?> getNextValue() {
    if (dirty) {
      dirty = false;
      observation.requestNext();
    }
    return latestContinuousFunction;
  }

  /**
   * Render the {@link #latestContinuousFunction latest continuous function}
   * into the {@link #getSeries() series} for the given range and resolution.
   * 
   * @param domain
   *          the range to render through in the domain
   * @param resolution
   *          the resolution to render at in the domain
   */
  public synchronized void render(Interval<Double> domain, int resolution) {
    SampledContinuousFunction<?, ?> sampledContinuousFunction = resampleLastRendered(
        getNextValue(),
        domain,
        resolution);

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
        dataTemp.add(
            new Data<>(
                sampledContinuousFunction.domain().getSample(i),
                sampledContinuousFunction.range().getSample(i)));
      }
      data.addAll(dataTemp);
    }
  }

  private <U extends Quantity<U>> SampledContinuousFunction<U, ?> resampleLastRendered(
      ContinuousFunction<U, ?> latestRenderedContinuousFunction,
      Interval<Double> domain,
      int resolution) {
    SampledDomain<U> resolvableDomain = new RegularSampledDomain<>(
        latestRenderedContinuousFunction.domain().getUnit(),
        resolution,
        resolution / (domain.getRightEndpoint() - domain.getLeftEndpoint()),
        domain.getLeftEndpoint());

    return latestRenderedContinuousFunction.resample(resolvableDomain);
  }

  /**
   * @return the continuous function backing the series
   */
  public ContinuousFunction<?, ?> getBackingContinuousFunction() {
    return sourceContinuousFunction;
  }

  /**
   * @return the latest continuous function prepared for rendering
   */
  public ContinuousFunction<?, ?> getLatestRenderedContinuousFunction() {
    return latestContinuousFunction;
  }

  /**
   * @return The series providing a view of the continuous function
   */
  public Series<Number, Number> getSeries() {
    return series;
  }
}
