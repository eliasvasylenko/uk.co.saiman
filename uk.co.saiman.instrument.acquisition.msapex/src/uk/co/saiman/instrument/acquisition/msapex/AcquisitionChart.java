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
 * This file is part of uk.co.saiman.instrument.acquisition.msapex.
 *
 * uk.co.saiman.instrument.acquisition.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.acquisition.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.acquisition.msapex;

import static uk.co.saiman.measurement.Units.count;
import static uk.co.saiman.measurement.Units.second;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import javafx.scene.layout.BorderPane;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.saiman.instrument.msapex.device.DevicePresentationService;
import uk.co.saiman.msapex.chart.ContinuousFunctionChart;
import uk.co.saiman.msapex.chart.ContinuousFunctionSeries;
import uk.co.saiman.msapex.chart.MetricTickUnits;
import uk.co.saiman.msapex.chart.QuantityAxis;
import uk.co.saiman.observable.Disposable;

public class AcquisitionChart extends BorderPane {
  private final AcquisitionDevice<?> device;
  private final ContinuousFunctionSeries<Time, Dimensionless> series;
  private volatile Disposable observation;

  public AcquisitionChart(
      AcquisitionDevice<?> device,
      DevicePresentationService presentationService) {
    this.device = device;

    ContinuousFunctionChart<Time, Dimensionless> chartController = new ContinuousFunctionChart<Time, Dimensionless>(
        new QuantityAxis<>(new MetricTickUnits<>(second())),
        new QuantityAxis<>(new MetricTickUnits<>(count())).setPaddingApplied(true));
    chartController.setTitle(presentationService.present(device).getLocalizedLabel());

    this.series = chartController.addSeries();

    setCenter(chartController);
  }

  public synchronized void open() {
    if (observation == null) {
      observation = device.dataEvents().observe(series::setContinuousFunction);
    }
  }

  public synchronized void close() {
    if (observation != null) {
      observation.cancel();
      observation = null;
    }
  }
}
