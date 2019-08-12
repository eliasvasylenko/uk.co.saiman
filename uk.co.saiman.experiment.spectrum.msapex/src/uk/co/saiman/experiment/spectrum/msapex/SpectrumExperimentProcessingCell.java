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

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.msapex.ExperimentStepCell.SUPPLEMENTAL_TEXT;
import static uk.co.saiman.experiment.processing.Processing.PROCESSING_VARIABLE;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.e4.ui.model.application.ui.MUIElement;

import javafx.scene.control.Label;
import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.event.ChangeVariableEvent;
import uk.co.saiman.experiment.processing.Processing;
import uk.co.saiman.experiment.processing.msapex.ProcessorCell;
import uk.co.saiman.experiment.spectrum.SpectrumProcessingExecutor;
import uk.co.saiman.experiment.spectrum.msapex.i18n.SpectrumProperties;

public class SpectrumExperimentProcessingCell {
  @Inject
  @Service
  private SpectrumProperties properties;
  @Inject
  private Step step;
  @Inject
  private ChildrenService children;

  @PostConstruct
  public void prepare(
      MCell cell,
      @Named(SUPPLEMENTAL_TEXT) Label supplemental,
      SpectrumProcessingExecutor conductor) {
    MCell parent = (MCell) (MUIElement) cell.getParent();

    parent.setLabel(properties.spectrumProcessingExperimentName().get());
    supplemental.setText(step.getInstruction().id().name());

    updateChildren(conductor);
  }

  @Inject
  @Optional
  public void updateVariables(ChangeVariableEvent event, SpectrumProcessingExecutor conductor) {
    if (event.step() == step) {
      updateChildren(conductor);
    }
  }

  public void updateChildren(SpectrumProcessingExecutor conductor) {
    children
        .setItems(
            ProcessorCell.ID,
            DataProcessor.class,
            step.getVariables().get(PROCESSING_VARIABLE).orElseThrow().steps().collect(toList()),
            r -> step.setVariable(PROCESSING_VARIABLE, v -> new Processing(r)));
  }
}
