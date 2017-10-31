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
package uk.co.saiman.msapex.experiment;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static uk.co.saiman.collection.StreamUtilities.streamOptional;
import static uk.co.saiman.collection.StreamUtilities.tryOptional;

import java.util.stream.Stream;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.Workspace;

public class ExperimentNodeAdapterFactory implements IAdapterFactory {
  private final IAdapterManager adapterManager;
  private final Workspace workspace;

  public ExperimentNodeAdapterFactory(IAdapterManager adapterManager, Workspace workspace) {
    this.adapterManager = adapterManager;
    this.workspace = workspace;
    adapterManager.registerAdapters(this, ExperimentNode.class);
  }

  public void unregister() {
    adapterManager.unregisterAdapters(this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
    ExperimentNode<?, ?> node = (ExperimentNode<?, ?>) adaptableObject;

    if (adapterType == ExperimentType.class) {
      return (T) node.getType();
    }

    if (adapterType == Experiment.class) {
      return (T) node.getExperiment();
    }

    if (adapterType == Workspace.class) {
      return (T) node.getWorkspace();
    }

    if (adapterType == node.getType().getStateType().getErasedType()) {
      return (T) node.getState();
    }

    if (adapterType == Result.class && node.getType().getResultType().isPresent()) {
      return (T) node.getResult();
    }

    if (adapterType == node
        .getType()
        .getResultType()
        .map(r -> r.getDataType().getErasedType())
        .orElse(null)) {
      return (T) node.getResult().get();
    }

    return (T) adapterManager.loadAdapter(node.getState(), adapterType.getName());
  }

  @Override
  public Class<?>[] getAdapterList() {
    return concat(
        of(ExperimentType.class, Experiment.class, Workspace.class),
        workspace
            .getRegisteredExperimentTypes()
            .map(type -> type.getStateType().getErasedType())
            .flatMap(this::getTransitive)).toArray(Class<?>[]::new);
  }

  public Stream<? extends Class<?>> getTransitive(Class<?> adapterType) {
    return concat(
        of(adapterType),
        of(adapterManager.computeAdapterTypes(adapterType)).distinct().flatMap(
            typeName -> streamOptional(
                tryOptional(() -> getClass().getClassLoader().loadClass(typeName)))));
  }
}
