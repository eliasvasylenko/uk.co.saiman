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

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import javafx.scene.Node;
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.fx.TreeCellContribution;
import uk.co.saiman.fx.TreeContribution;
import uk.co.saiman.fx.TreeItemData;
import uk.co.saiman.fx.TreeTextContribution;

/**
 * Contribution for root experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
@Component(
    service = TreeContribution.class,
    scope = ServiceScope.PROTOTYPE,
    property = Constants.SERVICE_RANKING + ":Integer=" + -80)
public class ExperimentResultContribution
    implements TreeTextContribution<Result<?>>, TreeCellContribution<Result<?>> {
  private static final String RESULT_PRESENT = "Present";

  @Inject
  @Localize
  ExperimentProperties text;

  @Override
  public <U extends Result<?>> String getText(TreeItemData<U> data) {
    return data.data().getResultType().getName();
  }

  @Override
  public <U extends Result<?>> String getSupplementalText(TreeItemData<U> data) {
    return "[" + data
        .data()
        .getData()
        .map(d -> Objects.toString(data.data().getResultDataPath()))
        .map(Object::toString)
        .orElse(text.missingResult().toString()) + "]";
  }

  @Override
  public <U extends Result<?>> Node configureCell(TreeItemData<U> data, Node content) {
    return configurePseudoClass(
        content,
        getClass().getSimpleName() + (data.data().getData().isPresent() ? RESULT_PRESENT : ""));
  }
}
