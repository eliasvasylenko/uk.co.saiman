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
 * This file is part of uk.co.saiman.experiment.definition.
 *
 * uk.co.saiman.experiment.definition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.definition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.definition;

import static java.lang.String.format;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static uk.co.saiman.collection.StreamUtilities.throwingMerger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentRelation;

public abstract class Definition<U extends ExperimentPath<U>, T extends Definition<U, T>> {
  private final List<StepDefinition> steps;
  private final Map<ExperimentId, StepDefinition> dependents;

  Definition(List<StepDefinition> steps, Map<ExperimentId, StepDefinition> dependents) {
    this.steps = steps;
    this.dependents = dependents;
  }

  List<StepDefinition> getSteps() {
    return steps;
  }

  Map<ExperimentId, StepDefinition> getDependents() {
    return dependents;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    Definition<?, ?> that = (Definition<?, ?>) obj;

    return Objects.equals(this.steps, that.steps);
  }

  @Override
  public int hashCode() {
    return Objects.hash(steps);
  }

  public Optional<StepDefinition> findSubstep(ExperimentId id) {
    return Optional.ofNullable(dependents.get(id));
  }

  public Stream<StepDefinition> substeps() {
    return steps.stream();
  }

  abstract T with(List<StepDefinition> steps, Map<ExperimentId, StepDefinition> dependents);

  T with(List<StepDefinition> steps) {
    return with(
        steps,
        steps
            .stream()
            .collect(toMap(StepDefinition::id, identity(), throwingMerger(), TreeMap::new)));
  }

  /**
   * Derive a new container with the step of the given ID removed, if it is
   * present.
   * 
   * @param id
   *          the ID of the step to remove
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withoutSubstep(ExperimentId id) {
    var dependents = new HashMap<>(this.dependents);
    var steps = new ArrayList<>(this.steps);

    steps.remove(dependents.remove(id));

    return with(steps, dependents);
  }

  /**
   * Derive a new container with all steps removed.
   * 
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withoutSubsteps() {
    return with(List.of(), Map.of());
  }

  public T withSubstep(StepDefinition step) {
    return withSubstep(steps.size(), step);
  }

  /**
   * Derive a new container including the given step. Any step sharing the same ID
   * with the new step will be replaced.
   * 
   * @param step
   *          the step to add
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withSubstep(int index, StepDefinition step) {
    var dependents = new HashMap<>(this.dependents);
    var steps = new ArrayList<>(this.steps);

    var previousStep = dependents.remove(step.id());
    if (previousStep != null) {
      var previousStepIndex = steps.indexOf(previousStep);
      if (previousStepIndex < index) {
        index--;
      }
      steps.remove(previousStepIndex);
    }

    dependents.put(step.id(), step);
    steps.add(index, step);

    return with(steps, dependents);
  }

  /**
   * Derive a new container including the given steps. Any steps sharing the same
   * ID with a new step will be replaced.
   * 
   * @param steps
   *          the steps to add
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withSubsteps(Collection<? extends StepDefinition> steps) {
    var dependents = new HashMap<>(this.dependents);
    var newSteps = new ArrayList<>(this.steps);

    for (StepDefinition step : steps) {
      newSteps.remove(dependents.remove(step.id()));
      dependents.put(step.id(), step);
      newSteps.add(step);
    }

    return with(newSteps, dependents);
  }

  /**
   * Derive a new container, optionally performing a removal and addition of an
   * step. If an step of the given ID is present, it will be removed from the
   * resulting container, and will be passed to the given function in an Optional,
   * otherwise an empty Optional will be passed. If the function returns an
   * Optional containing an step, that step will be present in the resulting
   * container.
   * 
   * @param id
   *          the ID of the step to replace
   * @param replacement
   *          a function accepting an optional over the existing step and
   *          returning its replacement
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withSubstep(
      ExperimentId id,
      Function<? super Optional<? extends StepDefinition>, ? extends Optional<? extends StepDefinition>> replacement) {
    var without = withoutSubstep(id);

    return replacement.apply(findSubstep(id)).map(without::withSubstep).orElse(without);
  }

  /**
   * Derive a new container, optionally modifying the contained steps.
   * 
   * @param transformation
   *          a function accepting a stream of all the currently present steps,
   *          and returning a stream of all the steps to include in the derived
   *          container
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withSubsteps(
      Function<? super Stream<? extends StepDefinition>, ? extends Stream<? extends StepDefinition>> transformation) {
    var steps = substeps().collect(Collectors.toList());
    return withoutSubsteps().withSubsteps(transformation.apply(steps.stream()).collect(toList()));
  }

  public Optional<StepDefinition> findSubstep(ExperimentPath<U> path) {
    if (path.isEmpty()) {
      return Optional.empty();
    }

    var ids = path.iterator();
    var id = ids.next();

    if (!(id instanceof ExperimentRelation.Dependent)) {
      throw new ExperimentDefinitionException(
          format("Cannot resolve step definition which is not direct descendent at %s", path));
    }

    var step = findSubstep(((ExperimentRelation.Dependent) id).id());
    while (ids.hasNext() && step.isPresent()) {
      step = step.flatMap(s -> s.findSubstep(((ExperimentRelation.Dependent) ids.next()).id()));
    }
    return step;
  }

  /**
   * Derive a new container, performing an optional replacement of the step at the
   * given path.
   * 
   * @param path
   *          the path of the step to replace
   * @param replacement
   *          a function accepting the existing step and returning its optional
   *          replacement, or an empty optional to indicate that the step should
   *          be removed from the derived container
   * @return An optional containing the derived container, or an empty optional if
   *         a step could not be found at the given path.
   */
  public Optional<T> withSubstep(
      ExperimentPath<U> path,
      Function<? super StepDefinition, ? extends Optional<? extends StepDefinition>> replacement) {
    if (path.isEmpty()) {
      throw new ExperimentDefinitionException(format("Cannot resolve step definition at %s", path));
    }

    var id = path.id(0);
    var subPath = path.relativeTo(id);
    var subStep = findSubstep(id);

    return (subPath.isEmpty()
        ? subStep.map(replacement::apply)
        : subStep.map(step -> step.withSubstep(subPath, replacement)))
            .map(step -> withSubstep(id, s -> step));
  }
}
