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
 * This file is part of uk.co.saiman.experiment.sample.msapex.
 *
 * uk.co.saiman.experiment.sample.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.sample.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.stage.msapex;

import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_AREA_ID;

import java.util.Objects;

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

public class SampleAreaCell {
  @Inject
  public SampleAreaCell(MCell cell, @Optional @Named(SAMPLE_AREA_ID) Object area) {
    if (area == null) {
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

    parent.setIconURI("fugue:size16/target.png");

    Label samplePlateLabel = new Label();
    node.getChildren().add(samplePlateLabel);
    HBox.setHgrow(samplePlateLabel, Priority.SOMETIMES);

    variables
        .get(MaldiSampleConstants.SAMPLE_AREA)
        .map(Objects::toString)
        .ifPresent(samplePlateLabel::setText);
  }
}
