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
import static uk.co.saiman.experiment.spectrum.SpectrumConductor.PROCESSING;
import static uk.co.saiman.msapex.experiment.ExperimentStepCell.SUPPLEMENTAL_TEXT;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.MUIElement;

import javafx.scene.control.Label;
import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.event.ChangeVariableEvent;
import uk.co.saiman.experiment.processing.Processing;
import uk.co.saiman.experiment.spectrum.SpectrumProcessingConductor;
import uk.co.saiman.msapex.experiment.processing.ProcessorCell;
import uk.co.saiman.msapex.experiment.spectrum.i18n.SpectrumProperties;

public class SpectrumExperimentProcessingCell {
  @Inject
  @Localize
  private SpectrumProperties properties;
  @Inject
  private Step step;
  @Inject
  private ChildrenService children;

  @PostConstruct
  public void prepare(
      Cell cell,
      @Named(SUPPLEMENTAL_TEXT) Label supplemental,
      SpectrumProcessingConductor conductor) {
    Cell parent = (Cell) (MUIElement) cell.getParent();

    parent.setLabel(properties.spectrumProcessingExperimentName().get());
    supplemental.setText(step.getInstruction().id());

    updateChildren(conductor);
  }

  @Inject
  @Optional
  public void updateVariables(ChangeVariableEvent event, SpectrumProcessingConductor conductor) {
    if (event.step() == step) {
      updateChildren(conductor);
    }
  }

  public void updateChildren(SpectrumProcessingConductor conductor) {
    children
        .setItems(
            ProcessorCell.ID,
            DataProcessor.class,
            step.getVariable(PROCESSING).steps().collect(toList()),
            r -> step.setVariable(PROCESSING, v -> new Processing(r)));
  }
}
