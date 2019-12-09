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
 * This file is part of uk.co.saiman.maldi.spectrum.
 *
 * uk.co.saiman.maldi.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.spectrum.msapex;

import static uk.co.saiman.experiment.msapex.ExperimentStepCell.SUPPLEMENTAL_PSEUDO_CLASS;
import static uk.co.saiman.maldi.spectrum.MaldiSpectrumConstants.SPECTRUM_ACQUISITION_COUNT;
import static uk.co.saiman.maldi.spectrum.MaldiSpectrumConstants.SPECTRUM_ACQUISITION_COUNT_ID;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.MUIElement;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.ui.ToBeRendered;
import uk.co.saiman.eclipse.ui.fx.EditableCellText;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.variables.Variables;

public class AcquisitionCountCell {
  @Inject
  EditableCellText acquisitionCountEditor;

  @ToBeRendered
  public static boolean render(
      @Optional @Named(SPECTRUM_ACQUISITION_COUNT_ID) Object acquisitionCount) {
    return acquisitionCount != null;
  }

  @Optional
  @PostConstruct
  public void prepare(HBox node, MCell cell, Step step, Variables variables) {
    var parent = (MCell) (MUIElement) cell.getParent();
    parent.setIconURI("fugue:size16/system-monitor.png");

    node.getChildren().add(acquisitionCountEditor);
    HBox.setHgrow(acquisitionCountEditor, Priority.SOMETIMES);

    step
        .getVariable(SPECTRUM_ACQUISITION_COUNT)
        .map(Objects::toString)
        .ifPresent(acquisitionCountEditor::setText);
    acquisitionCountEditor
        .setTryUpdate(name -> step.setVariable(SPECTRUM_ACQUISITION_COUNT, Integer.parseInt(name)));
    acquisitionCountEditor.getLabel().pseudoClassStateChanged(SUPPLEMENTAL_PSEUDO_CLASS, true);
  }
}
