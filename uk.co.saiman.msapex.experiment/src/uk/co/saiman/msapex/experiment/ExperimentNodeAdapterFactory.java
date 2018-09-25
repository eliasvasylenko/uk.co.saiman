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

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static uk.co.saiman.collection.StreamUtilities.flatMapRecursive;
import static uk.co.saiman.collection.StreamUtilities.streamNullable;
import static uk.co.saiman.collection.StreamUtilities.streamOptional;
import static uk.co.saiman.collection.StreamUtilities.tryOptional;

import java.util.Optional;
import java.util.function.Supplier;
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
  private final Supplier<? extends Stream<? extends ExperimentType<?, ?>>> experimentTypes;

  public ExperimentNodeAdapterFactory(
      IAdapterManager adapterManager,
      Supplier<? extends Stream<? extends ExperimentType<?, ?>>> experimentTypes) {
    this.adapterManager = adapterManager;
    this.experimentTypes = experimentTypes;
    adapterManager.registerAdapters(this, ExperimentNode.class);
  }

  public void unregister() {
    adapterManager.unregisterAdapters(this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
    ExperimentNode<?, ?> node = (ExperimentNode<?, ?>) adaptableObject;

    if (adapterType.isAssignableFrom(ExperimentType.class)) {
      return (T) node.getType();
    }

    if (adapterType.isAssignableFrom(Experiment.class)) {
      return (T) node.getExperiment();
    }

    if (adapterType.isAssignableFrom(Workspace.class)) {
      return (T) node.getWorkspace();
    }

    if (adapterType.isAssignableFrom(Result.class)
        && node.getType().getResultType().getErasedType() != void.class) {
      return (T) node.getResult();
    }

    if (adapterType.isAssignableFrom(node.getType().getStateType().getErasedType())) {
      return (T) node.getState();
    }

    if (adapterType.isAssignableFrom(node.getResult().getType().getErasedType())) {
      Optional<?> value = node.getResult().getValue();
      if (value.isPresent())
        return (T) value.get();
    }

    return (T) adapterManager.loadAdapter(node.getState(), adapterType.getName());
  }

  @Override
  public Class<?>[] getAdapterList() {
    return concat(
        of(ExperimentType.class, Experiment.class, Workspace.class, Result.class),
        experimentTypes
            .get()
            .map(type -> type.getStateType().getErasedType())
            .flatMap(this::getTransitive)).toArray(Class<?>[]::new);
  }

  private Stream<? extends Class<?>> getTransitive(Class<?> adapterType) {
    return concat(getSuperTypes(adapterType), getAdapterTypes(adapterType));
  }

  private Stream<Class<?>> getAdapterTypes(Class<?> adapterType) {
    return of(adapterManager.computeAdapterTypes(adapterType))
        .distinct()
        .flatMap(
            typeName -> streamOptional(
                tryOptional(() -> getClass().getClassLoader().loadClass(typeName))));
  }

  private Stream<Class<?>> getSuperTypes(Class<?> adapterType) {
    return flatMapRecursive(
        adapterType,
        t -> concat(streamNullable(t.getSuperclass()), Stream.of(t.getInterfaces())));
  }
}
