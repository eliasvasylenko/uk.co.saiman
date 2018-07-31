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
import static org.osgi.service.component.ComponentConstants.COMPONENT_NAME;
import static uk.co.saiman.eclipse.ui.TransferMode.COPY;
import static uk.co.saiman.eclipse.ui.TransferMode.DISCARD;
import static uk.co.saiman.eclipse.ui.TransferMode.MOVE;
import static uk.co.saiman.eclipse.ui.fx.TableService.setLabel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceRanking;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.ui.ListItems;
import uk.co.saiman.eclipse.ui.TransferIn;
import uk.co.saiman.eclipse.ui.TransferOut;
import uk.co.saiman.eclipse.ui.model.MCell;
import uk.co.saiman.eclipse.ui.model.MCellImpl;
import uk.co.saiman.eclipse.variable.NamedVariable;
import uk.co.saiman.experiment.processing.ProcessingProperties;
import uk.co.saiman.experiment.processing.Processor;
import uk.co.saiman.experiment.processing.ProcessorService;
import uk.co.saiman.msapex.experiment.persistence.JsonPersistedStateFormat;
import uk.co.saiman.properties.PropertyLoader;
import uk.co.saiman.property.Property;

@ServiceRanking(-100)
@Component(name = ProcessorListCell.ID, service = MCell.class)
public class ProcessorListCell extends MCellImpl {
  public static final String ID = "uk.co.saiman.experiment.processing.cell.list";
  public static final String PROCESSOR_ID = ID + ".processor";

  public ProcessorListCell() {
    super(ID, Contribution.class);
  }

  @Reference
  private ProcessorService processors;

  @Reference
  private PropertyLoader properties;

  @Reference(target = "(" + COMPONENT_NAME + "=" + ProcessorCell.ID + ")")
  public void setChild(MCell processor) {
    MCellImpl child = new MCellImpl(PROCESSOR_ID, null);
    child.setSpecialized(processor);
    child.setParent(this);
  }

  public class Contribution {
    @AboutToShow
    public void prepare(
        HBox node,
        @NamedVariable(ENTRY_DATA) Property<List<Processor<?>>> entry,
        ListItems children) {
      setLabel(node, properties.getProperties(ProcessingProperties.class).processing().toString());

      children
          .<Processor<?>>getConfiguration(PROCESSOR_ID)
          .setObjects(new ArrayList<>(entry.get()))
          .setDataFormat(
              new JsonPersistedStateFormat(),
              Processor::getState,
              processors::loadProcessor)
          .setDragHandlers(candidate -> {}, COPY)
          .setDragHandlers(candidate -> handleDrag(entry, candidate), MOVE, DISCARD)
          .setDropHandlers(candidate -> handleDrop(entry, candidate), MOVE, COPY);
    }

    private void handleDrop(
        Property<List<Processor<?>>> entry,
        TransferIn<? extends Processor<?>> candidate) {
      List<Processor<?>> data = new ArrayList<>(entry.get());

      int index;
      switch (candidate.position()) {
      case AFTER_CHILD:
        index = data.indexOf(candidate.adjacentItem()) + 1;
        break;
      case BEFORE_CHILD:
        index = data.indexOf(candidate.adjacentItem());
        break;
      default:
        index = data.size();
      }

      data.addAll(index, candidate.data().collect(toList()));
      entry.set(data);
    }

    void handleDrag(
        Property<List<Processor<?>>> entry,
        TransferOut<? extends Processor<?>> candidate) {
      List<Processor<?>> data = new ArrayList<>(entry.get());
      data.remove(candidate.data());
      entry.set(data);
    }
  }
}
