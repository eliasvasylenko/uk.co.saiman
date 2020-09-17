/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.instrument.vacuum.msapex.
 *
 * uk.co.saiman.instrument.vacuum.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.vacuum.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.vacuum.msapex;

import static uk.co.saiman.measurement.Units.pascal;
import static uk.co.saiman.measurement.Units.second;

import java.util.ArrayDeque;
import java.util.Deque;

import javax.measure.Quantity;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Time;

import javafx.scene.Node;
import uk.co.saiman.data.function.ArraySampledContinuousFunction;
import uk.co.saiman.data.function.IrregularSampledDomain;
import uk.co.saiman.instrument.msapex.device.DevicePresentationService;
import uk.co.saiman.instrument.vacuum.VacuumDevice;
import uk.co.saiman.instrument.vacuum.VacuumSample;
import uk.co.saiman.msapex.chart.ContinuousFunctionChart;
import uk.co.saiman.msapex.chart.ContinuousFunctionSeries;
import uk.co.saiman.msapex.chart.MetricTickUnits;
import uk.co.saiman.msapex.chart.QuantityAxis;

public class PressureChart {
  private final ContinuousFunctionChart<Time, Pressure> chart;
  private final Deque<VacuumSample> samples;

  public PressureChart(VacuumDevice vacuumDevice, DevicePresentationService presentationService) {
    this.chart = new ContinuousFunctionChart<Time, Pressure>(
        new QuantityAxis<>(new MetricTickUnits<>(second())),
        new QuantityAxis<>(new MetricTickUnits<>(pascal())).setPaddingApplied(true));
    this.chart.setTitle(presentationService.present(vacuumDevice).getLocalizedLabel());

    this.samples = new ArrayDeque<>();

    /*
     * Add latest data to chart controller
     */
    ContinuousFunctionSeries<Time, Pressure> series = chart.addSeries();
    vacuumDevice.sampleEvents().observe(sample -> updateSeries(series, sample));
  }

  private void updateSeries(ContinuousFunctionSeries<Time, Pressure> series, VacuumSample sample) {
    var function = new ArraySampledContinuousFunction<>(
        new IrregularSampledDomain<>(
            second().getUnit(),
            samples
                .stream()
                .map(VacuumSample::getMeasuredTime)
                .map(q -> q.to(second().getUnit()))
                .map(Quantity::getValue)
                .mapToDouble(Number::doubleValue)
                .toArray()),
        pascal().getUnit(),
        samples
            .stream()
            .map(VacuumSample::getMeasuredPressure)
            .map(q -> q.to(pascal().getUnit()))
            .map(Quantity::getValue)
            .mapToDouble(Number::doubleValue)
            .toArray());
    series.setContinuousFunction(function);
  }

  public Node getNode() {
    return chart;
  }
}
