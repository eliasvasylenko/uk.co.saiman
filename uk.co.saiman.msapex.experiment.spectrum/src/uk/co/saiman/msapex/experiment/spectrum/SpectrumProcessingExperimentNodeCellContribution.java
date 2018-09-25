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
import static uk.co.saiman.experiment.WorkspaceEventKind.STATE;
import static uk.co.saiman.msapex.experiment.ExperimentNodeCell.SUPPLEMENTAL_TEXT;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;

import javafx.scene.control.Label;
import uk.co.saiman.eclipse.adapter.AdaptClass;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.processing.Processing;
import uk.co.saiman.experiment.processing.ProcessorConfiguration;
import uk.co.saiman.experiment.spectrum.SpectrumResultConfiguration;
import uk.co.saiman.msapex.experiment.processing.ProcessorCell;

public class SpectrumProcessingExperimentNodeCellContribution {
  @Optional
  @Inject
  public void prepare(
      ExperimentNode<?, ?> data,
      Cell cell,
      @Named(SUPPLEMENTAL_TEXT) Label supplemental,
      @AdaptClass(ExperimentNode.class) SpectrumResultConfiguration state) {
    if (state != null) {
      cell.setLabel(data.getType().getName());
      supplemental.setText(state.getSpectrumName());
    }
  }

  public static class Processors {

    @Inject
    public void prepare(
        ExperimentNode<?, ?> data,
        Cell cell,
        ChildrenService children,
        @Optional @AdaptClass(ExperimentNode.class) SpectrumResultConfiguration state) {
      cell.setVisible(state != null);
      if (cell.isVisible()) {
        data
            .getWorkspace()
            .events()
            .filter(e -> e.getNode() == data)
            .filter(e -> e.getKind() == STATE)
            .take(1)
            .observe(o -> children.invalidate());

        children
            .setItems(
                ProcessorCell.ID,
                ProcessorConfiguration.class,
                state.getProcessing().processors().collect(toList()),
                r -> state.setProcessing(new Processing(new ArrayList<>(r))));
      }
    }
  }
}
