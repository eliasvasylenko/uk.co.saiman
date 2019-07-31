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
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
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
import uk.co.saiman.experiment.environment.SharedEnvironment;
import uk.co.saiman.experiment.msapex.i18n.ExperimentProperties;
import uk.co.saiman.experiment.msapex.step.provider.StepProvider;
import uk.co.saiman.experiment.msapex.step.provider.StepProviderDescriptor;
import uk.co.saiman.experiment.msapex.workspace.WorkspaceExperiment;
import uk.co.saiman.experiment.requirement.NoRequirement;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

/**
 * Track acquisition devices available through OSGi services and select which
 * device to display in the acquisition part.
 * 
 * @author Elias N Vasylenko
 */
public class AddStepsToExperimentMenu {
  @Inject
  @Service
  List<StepProviderDescriptor> providerDescriptors;
  @Inject
  Log log;
  @Inject
  @Localize
  ExperimentProperties text;

  @Inject
  SharedEnvironment environment;

  @AboutToShow
  void aboutToShow(
      List<MMenuElement> items,
      WorkspaceExperiment experiment,
      IEclipseContext context) {
    providerDescriptors
        .stream()
        .flatMap(descriptor -> createMenuItem(experiment, context, descriptor).stream())
        .forEach(items::add);
  }

  private Optional<MDirectMenuItem> createMenuItem(
      WorkspaceExperiment experiment,
      IEclipseContext context,
      StepProviderDescriptor descriptor) {
    var provider = ContextInjectionFactory.make(descriptor.getProviderClass(), context);

    if (!(provider.executor().mainRequirement() instanceof NoRequirement)) {
      return Optional.empty();
    }

    MDirectMenuItem moduleItem = MMenuFactory.INSTANCE.createDirectMenuItem();
    moduleItem.setLabel(descriptor.getLabel());
    moduleItem.setType(ItemType.PUSH);
    moduleItem.setObject(new Object() {
      @Execute
      public void execute(IEclipseContext context) {
        addStep(experiment, descriptor, provider);
      }
    });
    return Optional.of(moduleItem);
  }

  private void addStep(
      WorkspaceExperiment experiment,
      StepProviderDescriptor descriptor,
      StepProvider provider) {
    try {

      provider
          .createSteps(
              environment,
              new DefineStepImpl(experiment.experiment().getDefinition(), provider.executor()))
          .forEach(experiment.experiment()::attach);

    } catch (Exception e) {
      log.log(Level.ERROR, e);

      Alert alert = new Alert(AlertType.ERROR);
      DialogUtilities.addStackTrace(alert, e);
      alert.setTitle(text.attachStepFailedDialog().toString());
      alert.setHeaderText(text.attachStepFailedText(experiment, descriptor.getLabel()).toString());
      alert.setContentText(text.attachStepFailedDescription().toString());
      alert.showAndWait();
    }
  }
}
