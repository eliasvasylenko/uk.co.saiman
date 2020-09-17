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

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;

import javafx.scene.layout.BorderPane;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.variables.Variables;

public class MaldiStagePart {
  @Inject
  private BorderPane container;

  @Inject
  void setSamplePlate(@Optional SamplePlatePresenter presenter) {
    if (presenter != null) {
      container.setCenter(presenter.getWidget());
    } else {
      container.setCenter(null);
    }
  }
  
  @Inject
  void setStep(@Optional Step step, @Optional Variables variables) {
    if (step != null) {
      
    }
  }
}
