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
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.production.Product;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;
import uk.co.saiman.msapex.experiment.step.provider.StepProvider;
import uk.co.saiman.msapex.experiment.step.provider.StepProviderDescriptor;

/**
 * Track acquisition devices available through OSGi services and select which
 * device to display in the acquisition part.
 * 
 * @author Elias N Vasylenko
 */
public class AddStepsToStepMenu {
  @Inject
  @Service
  List<StepProviderDescriptor> providerDescriptors;
  @Inject
  Log log;
  @Inject
  @Localize
  ExperimentProperties text;

  @AboutToShow
  void aboutToShow(List<MMenuElement> items, Step step, IEclipseContext context) {
    providerDescriptors
        .stream()
        .filter(descriptor -> createProvider(step, descriptor, context).isPresent())
        .map(descriptor -> createMenuItem(step, descriptor))
        .forEach(items::add);
  }

  private Optional<StepProvider<? extends Product>> createProvider(
      Step step,
      StepProviderDescriptor descriptor,
      IEclipseContext context) {
    return ContextInjectionFactory
        .make(descriptor.getProviderClass(), context)
        .asDependent(step.getExecutor());
  }

  private <T extends Product> MDirectMenuItem createMenuItem(
      Step step,
      StepProviderDescriptor descriptor) {
    MDirectMenuItem moduleItem = MMenuFactory.INSTANCE.createDirectMenuItem();
    moduleItem.setLabel(descriptor.getLabel());
    moduleItem.setType(ItemType.PUSH);
    moduleItem.setObject(new Object() {
      @Execute
      public void execute(IEclipseContext context) {
        addStep(step, descriptor, createProvider(step, descriptor, context).get());
      }
    });
    return moduleItem;
  }

  private <T extends Product> void addStep(
      Step step,
      StepProviderDescriptor descriptor,
      StepProvider<T> provider) {
    try {
      provider
          .createSteps(new DefineStepImpl<>(step.getDefinition(), provider.executor()))
          .forEach(step::attach);

    } catch (Exception e) {
      log.log(Level.ERROR, e);

      Alert alert = new Alert(AlertType.ERROR);
      DialogUtilities.addStackTrace(alert, e);
      alert.setTitle(text.attachStepFailedDialog().toString());
      alert.setHeaderText(text.attachStepFailedText(step, descriptor.getLabel()).toString());
      alert.setContentText(text.attachStepFailedDescription().toString());
      alert.showAndWait();
    }
  }
}
