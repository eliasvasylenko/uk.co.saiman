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
 * This file is part of uk.co.saiman.experiment.procedure.
 *
 * uk.co.saiman.experiment.procedure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.procedure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.definition;

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

public abstract class StepContainer<T extends StepContainer<T>> {
  private final List<StepDefinition<?>> steps;
  private final Map<String, StepDefinition<?>> dependents;

  StepContainer(List<StepDefinition<?>> steps) {
    this(
        steps,
        steps
            .stream()
            .collect(toMap(StepDefinition::id, identity(), throwingMerger(), TreeMap::new)));
  }

  StepContainer(List<StepDefinition<?>> steps, Map<String, StepDefinition<?>> dependents) {
    this.steps = steps;
    this.dependents = dependents;
  }

  List<StepDefinition<?>> getSteps() {
    return steps;
  }

  Map<String, StepDefinition<?>> getDependents() {
    return dependents;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    StepContainer<?> that = (StepContainer<?>) obj;

    return Objects.equals(this.dependents, that.dependents);
  }

  @Override
  public int hashCode() {
    return Objects.hash(steps);
  }

  public Optional<StepDefinition<?>> findStep(String id) {
    return Optional.ofNullable(dependents.get(id));
  }

  public Stream<StepDefinition<?>> steps() {
    return steps.stream();
  }

  abstract T with(List<StepDefinition<?>> steps, Map<String, StepDefinition<?>> dependents);

  abstract T with(List<StepDefinition<?>> steps);

  /**
   * Derive a new container with the step of the given ID removed, if it is
   * present.
   * 
   * @param id the ID of the step to remove
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withoutStep(String id) {
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
  public T withoutSteps() {
    return with(List.of(), Map.of());
  }

  /**
   * Derive a new container including the given step. Any step sharing the same ID
   * with the new step will be replaced.
   * 
   * @param step the step to add
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withStep(StepDefinition<?> step) {
    var dependents = new HashMap<>(this.dependents);
    var steps = new ArrayList<>(this.steps);

    steps.remove(dependents.remove(step.id()));
    dependents.put(step.id(), step);
    steps.add(step);

    return with(steps, dependents);
  }

  /**
   * Derive a new container including the given steps. Any steps sharing the same
   * ID with a new step will be replaced.
   * 
   * @param steps the steps to add
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withSteps(Collection<? extends StepDefinition<?>> steps) {
    var dependents = new HashMap<>(this.dependents);
    var newSteps = new ArrayList<>(this.steps);

    for (StepDefinition<?> step : steps) {
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
   * @param id          the ID of the step to replace
   * @param replacement a function accepting an optional over the existing step
   *                    and returning its replacement
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withStep(
      String id,
      Function<Optional<StepDefinition<?>>, Optional<StepDefinition<?>>> replacement) {
    var without = withoutStep(id);

    return replacement.apply(findStep(id)).map(without::withStep).orElse(without);
  }

  /**
   * Derive a new container, optionally modifying the contained steps.
   * 
   * @param transformation a function accepting a stream of all the currently
   *                       present steps, and returning a stream of all the steps
   *                       to include in the derived container
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withSteps(
      Function<Stream<StepDefinition<?>>, Stream<StepDefinition<?>>> transformation) {
    var steps = steps().collect(Collectors.toList());
    return withoutSteps().withSteps(transformation.apply(steps.stream()).collect(toList()));
  }
}
