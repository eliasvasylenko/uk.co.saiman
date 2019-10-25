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

import static org.eclipse.e4.ui.workbench.modeling.EModelService.PRESENTATION;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MSnippetContainer;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.fx.core.di.Service;

import uk.co.saiman.experiment.msapex.i18n.ExperimentProperties;
import uk.co.saiman.log.Log;

/**
 * Track acquisition devices available through OSGi services and select which
 * device to display in the acquisition part.
 * 
 * @author Elias N Vasylenko
 */
public class AddStepsMenu {
  private static final String STEP_PROVIDER_TAG = "ExperimentStepProvider";

  @Inject
  Log log;
  @Inject
  @Service
  ExperimentProperties text;

  @AboutToShow
  void aboutToShow(List<MMenuElement> items, EModelService models, MApplication application) {
    models
        .findElements(application, MSnippetContainer.class, PRESENTATION, e -> true)
        .stream()
        .map(MSnippetContainer::getSnippets)
        .flatMap(List::stream)
        .filter(
            snippet -> snippet instanceof MMenuElement
                && snippet.getTags().contains(STEP_PROVIDER_TAG))
        .map(snippet -> (MMenuElement) models.cloneElement(snippet, null))
        .forEach(items::add);
  }
}
