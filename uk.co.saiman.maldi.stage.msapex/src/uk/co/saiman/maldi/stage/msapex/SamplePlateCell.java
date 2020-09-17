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
 * This file is part of uk.co.saiman.maldi.stage.msapex.
 *
 * uk.co.saiman.maldi.stage.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.stage.msapex;

import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_PLATE_ID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.MUIElement;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.maldi.sample.MaldiSampleConstants;

public class SamplePlateCell {
  @Inject
  public SamplePlateCell(MCell cell, @Optional @Named(SAMPLE_PLATE_ID) Object plate) {
    if (plate == null) {
      cell.setToBeRendered(false);
    }
  }

  @Optional
  @PostConstruct
  public void prepare(
      HBox node,
      MCell cell,
      Variables variables,
      SamplePlatePresentationService samplePlatePresenter) {
    var parent = (MCell) (MUIElement) cell.getParent();

    parent.setIconURI("fugue:size16/flask.png");

    Label samplePlateLabel = new Label();
    node.getChildren().add(samplePlateLabel);
    HBox.setHgrow(samplePlateLabel, Priority.SOMETIMES);

    variables
        .get(MaldiSampleConstants.SAMPLE_PLATE)
        .flatMap(samplePlatePresenter::getPresenter)
        .map(SamplePlatePresenter::getLocalizedLabel)
        .ifPresent(samplePlateLabel::setText);
  }
}
