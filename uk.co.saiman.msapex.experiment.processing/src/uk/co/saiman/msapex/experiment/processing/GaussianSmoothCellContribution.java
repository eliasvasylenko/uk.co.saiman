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
 * This file is part of uk.co.saiman.msapex.experiment.processing.
 *
 * uk.co.saiman.msapex.experiment.processing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment.processing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.processing;

import static java.lang.Double.parseDouble;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.osgi.service.event.Event;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.SaiUiEvents;
import uk.co.saiman.experiment.processing.GaussianSmooth;
import uk.co.saiman.experiment.processing.ProcessorConfiguration;

public class GaussianSmoothCellContribution {
  private final Label label = new Label();

  @Optional
  @PostConstruct
  public void prepare(HBox node, GaussianSmooth entry) {
    node.getChildren().add(label);
  }

  @Optional
  @Inject
  void update(GaussianSmooth entry) {
    label.setText(Double.toString(entry.getStandardDeviation()));
  }

  public static class StandardDeviation {
    @Inject
    private IEclipseContext context;
    @Inject
    private Cell cell;
    @Inject
    @Localize
    private ProcessingProperties properties;

    private final Label label = new Label();
    private final TextArea area = new TextArea();

    @Optional
    @PostConstruct
    public void prepare(HBox node, GaussianSmooth entry) {
      node.getChildren().add(label);
      node.getChildren().add(area);
      area.setVisible(false);
      label.setText(Double.toString(entry.getStandardDeviation()));
    }

    @Optional
    @Inject
    void prepare(GaussianSmooth entry, @Named(SaiUiEvents.Cell.TOPIC_EDITING) Event editing) {
      cell.setLabel(properties.standardDeviationLabel().get());

      /*
       * TODO we have events when editing is set ... but how do we know whether it was
       * cancelled or successful? Do we need to use an enum for the editing state
       * rather than just a boolean? Would it make sense as a state since it's really
       * just an event?
       */

      boolean isEditing = false;

      if (isEditing) {
        area.setVisible(true);
        label.setVisible(false);
        area.setText(Double.toString(entry.getStandardDeviation()));
      } else {
        area.setVisible(false);
        label.setVisible(true);
        context
            .set(
                ProcessorConfiguration.class,
                entry.withStandardDeviation(parseDouble(area.getText())));
      }
    }
  }
}
