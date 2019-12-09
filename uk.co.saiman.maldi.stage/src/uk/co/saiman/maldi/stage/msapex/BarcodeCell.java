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
 * This file is part of uk.co.saiman.maldi.stage.
 *
 * uk.co.saiman.maldi.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.stage.msapex;

import static uk.co.saiman.experiment.msapex.ExperimentStepCell.SUPPLEMENTAL_PSEUDO_CLASS;
import static uk.co.saiman.experiment.msapex.ExperimentStepCell.SUPPLEMENTAL_TEXT;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_PLATE_BARCODE;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_PLATE_BARCODE_ID;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import uk.co.saiman.eclipse.ui.ToBeRendered;
import uk.co.saiman.eclipse.ui.fx.EditableCellText;
import uk.co.saiman.experiment.Step;

public class BarcodeCell {
  @Inject
  EditableCellText barcodeEditor;

  @ToBeRendered
  public static boolean render(@Optional @Named(SAMPLE_PLATE_BARCODE_ID) Object barcode) {
    return barcode != null;
  }

  @Optional
  @PostConstruct
  public void prepare(
      HBox node,
      Step step,
      SamplePlatePresentationService samplePlatePresenter,
      @Named(SUPPLEMENTAL_TEXT) Label supplemental) {
    node.getChildren().add(barcodeEditor);
    HBox.setHgrow(barcodeEditor, Priority.SOMETIMES);

    step
        .getVariable(SAMPLE_PLATE_BARCODE)
        .flatMap(s -> s.map(Objects::toString))
        .ifPresent(barcodeEditor::setText);
    barcodeEditor
        .setTryUpdate(
            name -> step
                .setVariable(
                    SAMPLE_PLATE_BARCODE,
                    name.isBlank()
                        ? java.util.Optional.empty()
                        : java.util.Optional.ofNullable(Integer.parseInt(name))));
    barcodeEditor.getLabel().pseudoClassStateChanged(SUPPLEMENTAL_PSEUDO_CLASS, true);
  }
}
