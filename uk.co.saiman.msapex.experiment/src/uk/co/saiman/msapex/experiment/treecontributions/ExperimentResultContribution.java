/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.msapex.experiment.treecontributions;

import java.util.Objects;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.eclipse.treeview.TreeContribution;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.Result;

/**
 * Contribution for root experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
@Component(scope = ServiceScope.PROTOTYPE, property = Constants.SERVICE_RANKING + ":Integer=" + -80)
public class ExperimentResultContribution implements TreeContribution {
  private static final String RESULT_PRESENT = "Present";

  @Inject
  @Localize
  ExperimentProperties properties;

  @AboutToShow
  public void prepare(HBox node, TreeEntry<Result<?>> entry) {
    node.getChildren().add(new Label(entry.data().getType().getName()));

    node.getChildren().add(
        new Label(
            "[" + entry
                .data()
                .map(d -> Objects.toString(entry.data().getAbsoluteDataPath()))
                .map(Object::toString)
                .tryGet()
                .orElse(properties.missingResult().toString()) + "]"));

    configurePseudoClass(
        node,
        getClass().getSimpleName() + (entry.data().isValid() ? RESULT_PRESENT : ""));
  }
}
