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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.spectrum.SpectrumProperties;
import uk.co.saiman.msapex.chart.ContinuousFunctionChartController;
import uk.co.saiman.msapex.chart.ContinuousFunctionSeries;
import uk.co.saiman.observable.Invalidation;

public class SpectrumRawGraphEditorPart {
  @Inject
  @Localize
  SpectrumProperties properties;

  @Inject
  MDirtyable dirty;

  @FXML
  private ContinuousFunctionChartController spectrumGraphController;
  private ContinuousFunctionSeries series;

  @PostConstruct
  void postConstruct(
      BorderPane container,
      @LocalInstance FXMLLoader loaderProvider,
      IEclipseContext context,
      Result<Spectrum> result,
      Invalidation<Spectrum> value) {
    container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());

    series = spectrumGraphController.addSeries();
    result.getValue().map(Spectrum::getTimeData).ifPresent(series::setContinuousFunction);

    value(value);
  }

  @Inject
  void value(Invalidation<Spectrum> value) {
    if (value != null && series != null) {
      series.setContinuousFunction(value.map(Spectrum::getTimeData));
    }
  }
}
