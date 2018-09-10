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

import javax.inject.Inject;

import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.Workspace;

public class ExperimentTree {
  public static final String ID = "uk.co.saiman.msapex.experiment.tree";

  @Inject
  void initialize(Workspace workspace, ChildrenService children) {
    children
        .setItems(
            ExperimentNodeCell.ID,
            ExperimentNode.class,
            workspace.getExperiments().collect(toList()));

    workspace
        .events()
        .weakReference(this)
        .filter(e -> !e.message().getNode().getParent().isPresent())
        .observe(m -> children.invalidate());

    /*
     * TODO this model of "invalidating" and reinjecting the children parameter
     * would work ... but it doesn't naturally facilitate reuse of child model
     * elements.
     */
  }
}