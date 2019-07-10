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
 * This file is part of uk.co.saiman.experiment.spectrum.msapex.
 *
 * uk.co.saiman.experiment.spectrum.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.spectrum.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.spectrum.msapex;

import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;
import static uk.co.saiman.measurement.Units.count;
import static uk.co.saiman.measurement.Units.dalton;

import javax.inject.Inject;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.experiment.production.Result;
import uk.co.saiman.msapex.chart.ContinuousFunctionChart;
import uk.co.saiman.msapex.chart.ContinuousFunctionSeries;
import uk.co.saiman.msapex.chart.MetricTickUnits;
import uk.co.saiman.msapex.chart.QuantityAxis;

public class SpectrumGraphEditorPart {
  @FXML
  private BorderPane spectrumGraphPane;

  private final ContinuousFunctionSeries<Mass, Dimensionless> series;

  @Inject
  SpectrumGraphEditorPart(BorderPane container, @LocalInstance FXMLLoader loaderProvider) {
    container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());

    ContinuousFunctionChart<Mass, Dimensionless> spectrumGraphController = new ContinuousFunctionChart<>(
        new QuantityAxis<>(new MetricTickUnits<>(dalton())),
        new QuantityAxis<>(new MetricTickUnits<>(count())).setPaddingApplied(true));
    spectrumGraphPane.setCenter(spectrumGraphController);

    series = spectrumGraphController.addSeries();
  }

  @Inject
  @Optional
  void value(Result<Spectrum> value) {
    if (value != null) {
      series.setContinuousFunction(() -> value.value().map(Spectrum::getMassData).orElse(null));
    }
  }
}
