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
package uk.co.saiman.msapex.experiment.workspace;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static uk.co.saiman.collection.StreamUtilities.flatMapRecursive;
import static uk.co.saiman.collection.StreamUtilities.streamNullable;
import static uk.co.saiman.collection.StreamUtilities.streamOptional;
import static uk.co.saiman.collection.StreamUtilities.tryOptional;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentStep;
import uk.co.saiman.experiment.Procedure;
import uk.co.saiman.experiment.Result;

public class ExperimentNodeAdapterFactory implements IAdapterFactory {
  private final IAdapterManager adapterManager;
  private final Supplier<? extends Stream<? extends Procedure<?>>> experimentTypes;

  public ExperimentNodeAdapterFactory(
      IAdapterManager adapterManager,
      Supplier<? extends Stream<? extends Procedure<?>>> experimentTypes) {
    this.adapterManager = adapterManager;
    this.experimentTypes = experimentTypes;
    adapterManager.registerAdapters(this, ExperimentStep.class);
  }

  public void unregister() {
    adapterManager.unregisterAdapters(this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
    ExperimentStep<?> node = (ExperimentStep<?>) adaptableObject;

    if (adapterType.isAssignableFrom(Procedure.class)) {
      return (T) node.getProcedure();
    }

    if (adapterType.isAssignableFrom(Experiment.class)) {
      return (T) node.getExperiment();
    }

    if (adapterType.isAssignableFrom(node.getProcedure().getVariablesType().getErasedType())) {
      return (T) node.getVariables();
    }

    return (T) adapterManager.loadAdapter(node.getVariables(), adapterType.getName());
  }

  @Override
  public Class<?>[] getAdapterList() {
    return concat(
        of(Procedure.class, Experiment.class, Workspace.class, Result.class),
        experimentTypes
            .get()
            .map(type -> type.getVariablesType().getErasedType())
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
