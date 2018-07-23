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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceRanking;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.treeview.Contributor;
import uk.co.saiman.eclipse.treeview.PseudoClassContributor;
import uk.co.saiman.eclipse.treeview.TreeContribution;
import uk.co.saiman.eclipse.treeview.TreeDragCandidate;
import uk.co.saiman.eclipse.treeview.TreeDropCandidate;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.eclipse.treeview.TreeEntryChild;
import uk.co.saiman.eclipse.treeview.TreeEntryChildren;
import uk.co.saiman.eclipse.treeview.TreeEntryClipboard;
import uk.co.saiman.experiment.processing.ProcessingProperties;
import uk.co.saiman.experiment.processing.Processor;
import uk.co.saiman.experiment.processing.ProcessorService;
import uk.co.saiman.msapex.experiment.persistence.JsonPersistedStateFormat;
import uk.co.saiman.properties.PropertyLoader;

@ServiceRanking(-100)
@Component
public class ProcessorListContribution implements TreeContribution {
  private final Contributor pseudoClass = new PseudoClassContributor(getClass().getSimpleName());

  @Reference
  private ProcessorService processors;

  @Reference
  private PropertyLoader properties;

  @AboutToShow
  public void prepare(
      HBox node,
      TreeEntry<List<Processor<?>>> entry,
      TreeEntryChildren children,
      TreeEntryClipboard<Processor<?>> dragAndDrop) {
    setLabel(node, properties.getProperties(ProcessingProperties.class).processing().toString());

    entry
        .data()
        .stream()
        .map(Processor::getRerefence)
        .map(TreeEntryChild::typedChild)
        .forEach(children::add);

    dragAndDrop
        .addDataFormat(
            new JsonPersistedStateFormat(),
            Processor::getState,
            processors::loadProcessor);

    dragAndDrop.addDragHandler(candidate -> {}, COPY);
    dragAndDrop.addDragHandler(candidate -> handleDrag(entry, candidate), MOVE, DISCARD);
    dragAndDrop.addDropHandler(candidate -> handleDrop(entry, candidate), MOVE, COPY);

    pseudoClass.configureCell(node);
  }

  private void handleDrop(
      TreeEntry<List<Processor<?>>> entry,
      TreeDropCandidate<? extends Processor<?>> candidate) {
    List<Processor<?>> data = new ArrayList<>(entry.data());

    int index;
    switch (candidate.position()) {
    case AFTER_CHILD:
      index = data.indexOf(candidate.adjacentEntry().data()) + 1;
      break;
    case BEFORE_CHILD:
      index = data.indexOf(candidate.adjacentEntry().data());
      break;
    default:
      index = data.size();
    }

    data.addAll(index, candidate.data().collect(toList()));
    entry.update(data);
  }

  void handleDrag(
      TreeEntry<List<Processor<?>>> entry,
      TreeDragCandidate<? extends Processor<?>> candidate) {
    List<Processor<?>> data = new ArrayList<>(entry.data());
    data.remove(candidate.data());
    entry.update(data);
  }
}
