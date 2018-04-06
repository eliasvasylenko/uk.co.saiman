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
 * This file is part of uk.co.saiman.msapex.experiment.spectrum.
 *
 * uk.co.saiman.msapex.experiment.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.spectrum;

import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;

import javax.inject.Inject;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import org.eclipse.fx.core.di.LocalInstance;
import org.eclipse.fx.core.di.Service;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.msapex.chart.ContinuousFunctionChart;
import uk.co.saiman.msapex.chart.ContinuousFunctionSeries;
import uk.co.saiman.msapex.chart.MetricTickUnits;
import uk.co.saiman.msapex.chart.QuantityAxis;
import uk.co.saiman.observable.Invalidation;

public class SpectrumRawGraphEditorPart {
  @FXML
  private BorderPane spectrumGraphPane;

  private final ContinuousFunctionSeries<Time, Dimensionless> series;

  @Inject
  SpectrumRawGraphEditorPart(
      BorderPane container,
      @LocalInstance FXMLLoader loaderProvider,
      @Service Units units) {
    container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());

    ContinuousFunctionChart<Time, Dimensionless> spectrumGraphController = new ContinuousFunctionChart<>(
        new QuantityAxis<>(new MetricTickUnits<>(units, Units::second)),
        new QuantityAxis<>(new MetricTickUnits<>(units, Units::count)).setPaddingApplied(true));
    spectrumGraphPane.setCenter(spectrumGraphController);

    series = spectrumGraphController.addSeries();
  }

  @Inject
  void value(Invalidation<Spectrum> value) {
    if (value != null) {
      series.setContinuousFunction(value.map(Spectrum::getTimeData));
    }
  }
}
