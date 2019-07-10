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
 * This file is part of uk.co.saiman.experiment.processing.msapex.
 *
 * uk.co.saiman.experiment.processing.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.processing.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.processing.msapex;

import static uk.co.saiman.experiment.msapex.ExperimentStepCell.SUPPLEMENTAL_PSEUDO_CLASS;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;

import javafx.scene.layout.HBox;
import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.data.function.processing.GaussianSmooth;
import uk.co.saiman.eclipse.ui.fx.EditableCellText;

public class StandardDeviationCell {
  @Inject
  EditableCellText editor;
  @Inject
  IEclipseContext context;

  @Optional
  @PostConstruct
  public void prepare(HBox node, GaussianSmooth entry) {
    node.getChildren().add(editor);
    editor.getLabel().pseudoClassStateChanged(SUPPLEMENTAL_PSEUDO_CLASS, true);
  }

  @Optional
  @Inject
  void update(GaussianSmooth entry) {
    if (entry != null) {
      editor.setText(Double.toString(entry.getStandardDeviation()));
      editor
          .setUpdate(
              value -> context
                  .modify(
                      DataProcessor.class,
                      entry.withStandardDeviation(Double.parseDouble(value))));
    }
  }
}
