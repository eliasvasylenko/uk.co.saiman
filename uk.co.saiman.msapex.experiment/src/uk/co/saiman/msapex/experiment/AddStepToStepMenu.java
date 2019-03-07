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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.fx.core.di.Service;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import uk.co.saiman.eclipse.dialog.DialogUtilities;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.ConductorService;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;

/**
 * Track acquisition devices available through OSGi services and select which
 * device to display in the acquisition part.
 * 
 * @author Elias N Vasylenko
 */
public class AddStepToStepMenu {
  @Inject
  @Service
  ConductorService conductors;
  @Inject
  Log log;
  @Inject
  @Localize
  ExperimentProperties text;

  @AboutToShow
  void aboutToShow(List<MMenuElement> items, Step<?, ?> selectedNode) {
    conductors
        .conductors()
        .flatMap(p -> createMenuItem(selectedNode, p).stream())
        .forEach(items::add);
  }

  private <T extends Product> Optional<MDirectMenuItem> createMenuItem(
      Step<?, ?> step,
      Conductor<?, T> subProcedure) {
    var resources = subProcedure
        .requirement()
        .resolveDependencies(step.getConductor())
        .collect(toList());
    return !resources.isEmpty()
        ? Optional.of(createMenuItem(step, resources, subProcedure))
        : Optional.empty();
  }

  private <T extends Product> MDirectMenuItem createMenuItem(
      Step<?, ?> step,
      List<? extends Production<? extends T>> resources,
      Conductor<?, T> subProcedure) {
    MDirectMenuItem moduleItem = MMenuFactory.INSTANCE.createDirectMenuItem();
    moduleItem.setLabel(conductors.getId(subProcedure));
    moduleItem.setType(ItemType.PUSH);
    moduleItem.setObject(new Object() {
      @Execute
      public void execute() {
        addNode(step, resources, subProcedure);
      }
    });
    return moduleItem;
  }

  private <T extends Product> void addNode(
      Step<?, ?> step,
      List<? extends Production<? extends T>> resources,
      Conductor<?, T> subProcedure) {
    Production<? extends T> resource;

    if (resources.size() == 1) {
      resource = resources.get(0);
    } else {
      // TODO open a dialog box to select which resource to attach to.
      throw new UnsupportedOperationException();
    }

    try {
      step.attach(resource, Instruction.define(subProcedure));
    } catch (Exception e) {
      log.log(Level.ERROR, e);

      Alert alert = new Alert(AlertType.ERROR);
      DialogUtilities.addStackTrace(alert, e);
      alert.setTitle(text.attachNodeFailedDialog().toString());
      alert.setHeaderText(text.attachNodeFailedText(step, resource, subProcedure).toString());
      alert.setContentText(text.attachNodeFailedDescription().toString());
      alert.showAndWait();
    }
  }
}
