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
package uk.co.saiman.experiment.procedure;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptyNavigableMap;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ProductIndex;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.product.Nothing;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;

public abstract class Instructions<T extends Instructions<T, U>, U extends ExperimentPath<U>> {
  /*
   * We store the experiment hierarchy as flat map. Because experiment paths are
   * comparable, the ordering is equivalent to an alphabetical, depth-first
   * traversal of the path hierarchy.
   */
  private final NavigableMap<ExperimentPath<U>, ExperimentLocation> instructions;

  Instructions() {
    this(emptyNavigableMap());
  }

  Instructions(NavigableMap<ExperimentPath<U>, ExperimentLocation> instructions) {
    this.instructions = instructions;
  }

  abstract ExperimentPath<U> getExperimentPath();

  NavigableMap<ExperimentPath<U>, ExperimentLocation> getInstructions() {
    return instructions;
  }

  Map<ProductPath<U>, ProductLocation> getDependencies() {
    return dependencies;
  }

  public Stream<ExperimentPath<U>> instructions() {
    return instructions.keySet().stream();
  }

  public Stream<ProductPath<U>> products(ExperimentPath<U> path) {
    return Optional
        .ofNullable(instructions.get(path))
        .map(l -> dependencyPaths(path, l))
        .orElseGet(Stream::empty);
  }

  private Stream<ProductPath<U>> dependencyPaths(
      ExperimentPath<U> path,
      ExperimentLocation location) {
    return location
        .instruction()
        .conductor()
        .products()
        .map(Production::id)
        .map(s -> path.resolve(ProductIndex.define(s)))
        .filter(dependencies::containsKey);
  }

  public Optional<Instruction> instruction(ExperimentPath<?> path) {
    return Optional.ofNullable(instructions.get(path)).map(e -> e.instruction());
  }

  public Stream<ExperimentPath<U>> independentInstructions() {
    return Optional
        .ofNullable(dependencies.get(null))
        .map(d -> d.dependents().map(getExperimentPath()::resolve))
        .orElseGet(Stream::empty);
  }

  public Stream<ExperimentPath<U>> dependentInstructions(ProductPath<U> path) {
    return Optional
        .ofNullable(dependencies.get(path))
        .map(d -> d.dependents().map(path.getExperimentPath()::resolve))
        .orElseGet(Stream::empty);
  }

  public Stream<ExperimentPath<U>> dependentInstructions(ExperimentPath<U> path) {
    return products(path).flatMap(this::dependentInstructions);
  }

  public T withInstruction(ProductPath<U> path, Instruction instruction) {
    return withInstruction(path, -1, instruction);
  }

  public T withInstruction(ProductPath<U> path, long index, Instruction instruction) {
    return withInstruction(Optional.of(path), index, instruction);
  }

  T withInstruction(long index, Instruction instruction) {
    return withInstruction(Optional.empty(), index, instruction);
  }

  private T withInstruction(
      Optional<ProductPath<U>> productPath,
      long index,
      Instruction instruction) {
    var instructions = new TreeMap<>(this.instructions);
    var dependencies = new HashMap<>(this.dependencies);

    addInstruction(instructions, dependencies, productPath, index, requireNonNull(instruction));

    return withInstructions(instructions, dependencies);
  }

  public <W extends Product> T withTemplate(
      Dependency<? extends W, U> dependency,
      Template<W> template) {
    return withTemplate(dependency, -1, template);
  }

  public <W extends Product> T withTemplate(
      Dependency<? extends W, U> dependency,
      long index,
      Template<W> template) {
    return withTemplate(
        Optional.of(validateProductPath(dependency)),
        index,
        requireNonNull(template));
  }

  T withTemplate(long index, Template<Nothing> template) {
    return withTemplate(Optional.empty(), index, template);
  }

  private T withTemplate(Optional<ProductPath<U>> productPath, long index, Template<?> template) {
    var instructions = new TreeMap<>(this.instructions);
    var dependencies = new HashMap<>(this.dependencies);

    ExperimentPath<U> experimentPath = productPath
        .map(ProductPath::getExperimentPath)
        .orElseGet(this::getExperimentPath)
        .resolve(template.id());

    if (instructions.containsKey(experimentPath)) {
      throw new ProcedureException(
          format(
              "Cannot expand template %s at path %s, instruction already present in %s",
              template,
              experimentPath,
              this));
    }

    addInstruction(
        instructions,
        dependencies,
        productPath,
        index,
        requireNonNull(template.instruction()));

    template
        .getInstructions()
        .entrySet()
        .stream()
        .map(e -> Map.entry(e.getKey(), e.getValue()))
        .forEach(e -> instructions.put(experimentPath.resolve(e.getKey()).get(), e.getValue()));
    template
        .getDependencies()
        .entrySet()
        .stream()
        .map(e -> e)
        .forEach(e -> dependencies.put(experimentPath.resolve(e.getKey()).get(), e.getValue()));

    return withInstructions(instructions, dependencies);
  }

  public T withoutInstruction(U experimentPath) {
    var instructions = new TreeMap<>(this.instructions);
    var dependencies = new HashMap<>(this.dependencies);

    removeInstruction(instructions, dependencies, experimentPath);

    return withInstructions(instructions, dependencies);
  }

  abstract T withInstructions(
      NavigableMap<ExperimentPath<U>, ExperimentLocation> instructions,
      Map<ProductPath<U>, ProductLocation> dependencies);

  private ProductPath<U> validateProductPath(Dependency<?, U> dependency) {
    requireNonNull(dependency);
    var experiment = instructions.get(dependency.getExperimentPath());
    if (experiment == null) {
      throw new ProcedureException(
          format("Cannot resolve dependency %s amongst instructions %s", dependency, this));
    }
    Productions
        .production(experiment.instruction().conductor(), dependency.getProduction().id())
        .filter(p -> p.equals(dependency.getProduction()))
        .orElseThrow(
            () -> new ProcedureException(
                format(
                    "Cannot resolve dependency %s against instruction %s",
                    dependency,
                    experiment.instruction())));
    return dependency.getProductPath();
  }

  private void addInstruction(
      TreeMap<ExperimentPath<U>, ExperimentLocation> instructionMap,
      HashMap<ProductPath<U>, ProductLocation> dependencyMap,
      Optional<ProductPath<U>> productPath,
      long index,
      Instruction instruction) {
    ExperimentPath<U> experimentPath = productPath
        .map(ProductPath::getExperimentPath)
        .orElseGet(this::getExperimentPath)
        .resolve(instruction.id());

    instructionMap.computeIfPresent(experimentPath, (e, l) -> l.withInstruction(instruction));

    dependencyMap
        .compute(
            productPath.orElse(null),
            (p, l) -> Optional
                .ofNullable(l)
                .orElseGet(ProductLocation::new)
                .withDependent(instruction.id()));
  }

  private void removeInstruction(
      TreeMap<ExperimentPath<U>, ExperimentLocation> instructionMap,
      HashMap<ProductPath<U>, ProductLocation> dependencyMap,
      U path) {
    var higherPaths = instructionMap.navigableKeySet().tailSet(path).iterator();
    while (higherPaths.hasNext()) {
      var higherPath = higherPaths.next();
      if (higherPath.relativeTo(path).ancestorDepth() > 0) {
        break;
      }
      higherPaths.remove();
    }

    // TODO remove instructions with indirect dependencies
    throw new UnsupportedOperationException();
  }

  /*
   * TODO convert to value type & record type
   */
  static class ExperimentLocation {
    private final Instruction instruction;
    private final List<String> dependents;

    private ExperimentLocation(Instruction instruction) {
      this(instruction, List.of());
    }

    private ExperimentLocation(Instruction instruction, List<String> dependents) {
      this.instruction = instruction;
      this.dependents = dependents;
    }

    public Instruction instruction() {
      return instruction;
    }

    public ExperimentLocation withInstruction(Instruction instruction) {
      /*
       * TODO check for existing products which might become unknown, remove existing
       * unknowns which and on the new instruction.
       */

      throw new UnsupportedOperationException();
    }

    public Stream<String> dependents() {
      return dependents.stream();
    }

    public ExperimentLocation withDependent(String path) {
      List<String> dependents = new ArrayList<>(this.dependents.size() + 1);
      dependents.addAll(this.dependents);
      dependents.add(path);
      return new ExperimentLocation(instruction, dependents);
    }
  }
}
