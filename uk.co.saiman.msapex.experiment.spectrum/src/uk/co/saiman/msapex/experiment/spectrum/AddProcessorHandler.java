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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static uk.co.saiman.fx.FxUtilities.wrap;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.core.di.Service;

import javafx.scene.control.ChoiceDialog;
import uk.co.saiman.eclipse.adapter.AdaptClass;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.processing.Processing;
import uk.co.saiman.experiment.processing.ProcessorConfiguration;
import uk.co.saiman.experiment.spectrum.SpectrumProcessingConfiguration;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;
import uk.co.saiman.properties.Localized;

public class AddProcessorHandler {
  @CanExecute
  boolean canExecute(
      @Optional @AdaptClass(ExperimentNode.class) SpectrumProcessingConfiguration configuration) {
    return configuration != null;
  }

  @Execute
  void execute(
      @AdaptClass(ExperimentNode.class) SpectrumProcessingConfiguration configuration,
      @Service List<ProcessorConfiguration> processors,
      @Localize ExperimentProperties text) {

    requestProcessorType(
        processors,
        text.addSpectrumProcessor(),
        text.addSpectrumProcessorDescription())
            .ifPresent(processor -> addProcessor(configuration, processor));
  }

  private void addProcessor(SpectrumProcessingConfiguration configuration, ProcessorConfiguration processor) {
    configuration
        .setProcessing(
            new Processing(
                concat(configuration.getProcessing().processors(), Stream.of(processor))
                    .collect(toList())));
  }

  static java.util.Optional<ProcessorConfiguration> requestProcessorType(
      @Service List<ProcessorConfiguration> processors,
      Localized<String> title,
      Localized<String> header) {
    ChoiceDialog<ProcessorConfiguration> nameDialog = processors.isEmpty()
        ? new ChoiceDialog<>()
        : new ChoiceDialog<>(processors.get(0), processors);
    nameDialog.titleProperty().bind(wrap(title));
    nameDialog.headerTextProperty().bind(wrap(header));

    return nameDialog.showAndWait();
  }
}
