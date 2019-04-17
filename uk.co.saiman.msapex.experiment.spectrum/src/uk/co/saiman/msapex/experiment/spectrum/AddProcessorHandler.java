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

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.fx.FxUtilities.wrap;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.core.di.Service;

import javafx.scene.control.ChoiceDialog;
import uk.co.saiman.eclipse.adapter.AdaptClass;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.processing.Processing;
import uk.co.saiman.experiment.processing.ProcessingService;
import uk.co.saiman.experiment.processing.ProcessingStrategy;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;
import uk.co.saiman.properties.Localized;

public class AddProcessorHandler {
  @Service
  @Inject
  private ProcessingService processingService;

  @Inject
  private IEclipseContext context;

  @CanExecute
  boolean canExecute(@Optional @AdaptClass(Step.class) Processing configuration) {
    return configuration != null;
  }

  @Execute
  void execute(
      @AdaptClass(Step.class) Processing configuration,
      @Localize ExperimentProperties text) {
    requestProcessorType(text.addSpectrumProcessor(), text.addSpectrumProcessorDescription())
        .ifPresent(
            processor -> context
                .set(Processing.class, configuration.withStep(processor.createProcessor())));
  }

  private java.util.Optional<ProcessingStrategy<?>> requestProcessorType(
      Localized<String> title,
      Localized<String> header) {
    List<ProcessingStrategy<?>> strategies = processingService.strategies().collect(toList());

    ChoiceDialog<ProcessingStrategy<?>> nameDialog = strategies.isEmpty()
        ? new ChoiceDialog<>()
        : new ChoiceDialog<>(strategies.get(0), strategies);
    nameDialog.titleProperty().bind(wrap(title));
    nameDialog.headerTextProperty().bind(wrap(header));

    return nameDialog.showAndWait();
  }
}
