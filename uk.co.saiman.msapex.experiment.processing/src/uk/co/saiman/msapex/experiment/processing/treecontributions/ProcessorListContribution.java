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
package uk.co.saiman.msapex.experiment.processing.treecontributions;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.eclipse.treeview.DefaultContribution.setLabel;
import static uk.co.saiman.eclipse.treeview.TreeTransferMode.COPY;
import static uk.co.saiman.eclipse.treeview.TreeTransferMode.DISCARD;
import static uk.co.saiman.eclipse.treeview.TreeTransferMode.MOVE;

import java.util.List;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.treeview.Contributor;
import uk.co.saiman.eclipse.treeview.PseudoClassContributor;
import uk.co.saiman.eclipse.treeview.TreeChildren;
import uk.co.saiman.eclipse.treeview.TreeClipboard;
import uk.co.saiman.eclipse.treeview.TreeContribution;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.experiment.persistence.json.JsonPersistedStateFormat;
import uk.co.saiman.experiment.processing.ProcessingProperties;
import uk.co.saiman.experiment.processing.ProcessorService;
import uk.co.saiman.experiment.processing.ProcessorState;
import uk.co.saiman.text.properties.PropertyLoader;

@Component(property = Constants.SERVICE_RANKING + ":Integer=" + -100)
public class ProcessorListContribution implements TreeContribution {
  private final Contributor pseudoClass = new PseudoClassContributor(getClass().getSimpleName());

  @Reference
  ProcessorService processors;

  @Reference
  PropertyLoader properties;

  @AboutToShow
  public void prepare(
      HBox node,
      TreeChildren children,
      TreeEntry<List<? extends ProcessorState>> entry,
      @Optional TreeEntry<List<ProcessorState>> mutableEntry,
      TreeClipboard<ProcessorState> dragAndDrop) {
    setLabel(node, properties.getProperties(ProcessingProperties.class).processing().toString());

    entry.data().forEach(children::addChild);

    dragAndDrop
        .addDataFormat(
            new JsonPersistedStateFormat(),
            ProcessorState::getPersistedState,
            processors::loadProcessorState);
    dragAndDrop.addDragHandler(candidate -> {}, COPY);
    if (mutableEntry != null) {
      dragAndDrop
          .addDragHandler(candidate -> mutableEntry.data().remove(candidate.data()), MOVE, DISCARD);
      dragAndDrop.addDropHandler(candidate -> {
        int index;
        switch (candidate.position()) {
        case AFTER_CHILD:
          index = entry.data().indexOf(candidate.adjacentEntry().data()) + 1;
          break;
        case BEFORE_CHILD:
          index = entry.data().indexOf(candidate.adjacentEntry().data());
          break;
        default:
          index = entry.data().size();
        }
        mutableEntry.data().addAll(index, candidate.data().collect(toList()));
      }, MOVE, COPY);
    }

    pseudoClass.configureCell(node);
  }
}
