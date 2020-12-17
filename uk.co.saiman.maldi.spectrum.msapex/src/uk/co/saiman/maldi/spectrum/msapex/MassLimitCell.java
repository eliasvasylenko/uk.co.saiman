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
 * This file is part of uk.co.saiman.maldi.spectrum.msapex.
 *
 * uk.co.saiman.maldi.spectrum.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.spectrum.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.spectrum.msapex;

import static uk.co.saiman.experiment.msapex.ExperimentStepCell.SUPPLEMENTAL_PSEUDO_CLASS;
import static uk.co.saiman.maldi.spectrum.MaldiSpectrumConstants.SPECTRUM_MASS_LIMIT;
import static uk.co.saiman.maldi.spectrum.MaldiSpectrumConstants.SPECTRUM_MASS_LIMIT_ID;
import static uk.co.saiman.measurement.Quantities.quantityFormat;
import static uk.co.saiman.measurement.Units.dalton;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.measure.quantity.Mass;

import org.eclipse.e4.core.di.annotations.Optional;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.ui.ToBeRendered;
import uk.co.saiman.eclipse.ui.fx.EditableCellText;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.variables.Variables;

public class MassLimitCell {
  @Inject
  EditableCellText massLimitEditor;

  @ToBeRendered
  public static boolean render(@Optional @Named(SPECTRUM_MASS_LIMIT_ID) Object massLimit) {
    return massLimit != null;
  }

  @Optional
  @PostConstruct
  public void prepare(HBox node, MCell cell, Step step, Variables variables) {
    node.getChildren().add(massLimitEditor);
    HBox.setHgrow(massLimitEditor, Priority.SOMETIMES);

    step.getVariables().get(SPECTRUM_MASS_LIMIT).map(quantityFormat()::format).ifPresent(massLimitEditor::setText);
    massLimitEditor
        .setTryUpdate(
            name -> step
                .setVariable(
                    SPECTRUM_MASS_LIMIT,
                    quantityFormat().parse(name).asType(Mass.class).to(dalton().getUnit())));
    massLimitEditor.getLabel().pseudoClassStateChanged(SUPPLEMENTAL_PSEUDO_CLASS, true);
  }
}
