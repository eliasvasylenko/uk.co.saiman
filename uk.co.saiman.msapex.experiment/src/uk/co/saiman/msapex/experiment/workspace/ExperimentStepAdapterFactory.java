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

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.Variable;

public class ExperimentStepAdapterFactory implements IAdapterFactory {
  private final IAdapterManager adapterManager;
  private final Supplier<? extends Stream<? extends Conductor<?>>> experimentTypes;

  public ExperimentStepAdapterFactory(
      IAdapterManager adapterManager,
      Supplier<? extends Stream<? extends Conductor<?>>> experimentTypes) {
    this.adapterManager = adapterManager;
    this.experimentTypes = experimentTypes;
    adapterManager.registerAdapters(this, Step.class);
  }

  public void unregister() {
    adapterManager.unregisterAdapters(this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
    Step node = (Step) adaptableObject;

    if (adapterType.isAssignableFrom(Conductor.class)) {
      return (T) node.getConductor();
    }

    if (adapterType.isAssignableFrom(Experiment.class)) {
      return (T) node.getExperiment();
    }

    var variable = node
        .getConductor()
        .variables()
        .filter(v -> adapterType.isAssignableFrom(v.type()))
        .findAny()
        .flatMap(node.getInstruction()::variable);

    return (T) variable.orElse(null);
  }

  @Override
  public Class<?>[] getAdapterList() {
    return concat(
        of(Conductor.class, Experiment.class),
        experimentTypes
            .get()
            .flatMap(Conductor::variables)
            .map(Variable::type)
            .flatMap(this::getTransitive)).toArray(Class<?>[]::new);
  }

  private Stream<? extends Class<?>> getTransitive(Class<?> adapterType) {
    return getSuperTypes(adapterType);
  }

  private Stream<Class<?>> getSuperTypes(Class<?> adapterType) {
    return flatMapRecursive(
        adapterType,
        t -> concat(streamNullable(t.getSuperclass()), Stream.of(t.getInterfaces())));
  }
}
