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

import static uk.co.saiman.reflection.token.TypedReference.typedObject;

import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.fx.TreeChildContribution;
import uk.co.saiman.fx.TreeContribution;
import uk.co.saiman.fx.TreeItemData;
import uk.co.saiman.reflection.token.TypedReference;

@Component(service = TreeContribution.class, scope = ServiceScope.PROTOTYPE)
public class WorkspaceContribution implements TreeChildContribution<Workspace> {
  @Override
  public <U extends Workspace> Stream<TypedReference<?>> getChildren(TreeItemData<U> data) {
    return data.data().getExperiments().map(c -> typedObject(Experiment.class, c));
  }
}
