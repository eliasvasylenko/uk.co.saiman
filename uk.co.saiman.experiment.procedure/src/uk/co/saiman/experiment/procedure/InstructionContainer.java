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

public abstract class InstructionContainer<T extends InstructionContainer<T>> {
  private final List<Instruction<?>> instructions;
  private final Map<String, Instruction<?>> dependents;

  InstructionContainer(List<Instruction<?>> instructions) {
    this(
        instructions,
        instructions
            .stream()
            .collect(toMap(Instruction::id, identity(), throwingMerger(), TreeMap::new)));
  }

  InstructionContainer(List<Instruction<?>> instructions, Map<String, Instruction<?>> dependents) {
    this.instructions = instructions;
    this.dependents = dependents;
  }

  List<Instruction<?>> getInstructions() {
    return instructions;
  }

  Map<String, Instruction<?>> getDependents() {
    return dependents;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    InstructionContainer<?> that = (InstructionContainer<?>) obj;

    return Objects.equals(this.dependents, that.dependents);
  }

  @Override
  public int hashCode() {
    return Objects.hash(instructions);
  }

  public Optional<Instruction<?>> instruction(String id) {
    return Optional.ofNullable(dependents.get(id));
  }

  public Stream<Instruction<?>> instructions() {
    return instructions.stream();
  }

  abstract T with(List<Instruction<?>> instructions, Map<String, Instruction<?>> dependents);

  abstract T with(List<Instruction<?>> instructions);

  /**
   * Derive a new container with the instruction of the given ID removed, if it is
   * present.
   * 
   * @param id the ID of the instruction to remove
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withoutInstruction(String id) {
    var dependents = new HashMap<>(this.dependents);
    var instructions = new ArrayList<>(this.instructions);

    instructions.remove(dependents.remove(id));

    return with(instructions, dependents);
  }

  /**
   * Derive a new container with all instructions removed.
   * 
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withoutInstructions() {
    return with(List.of(), Map.of());
  }

  /**
   * Derive a new container including the given instruction. Any instruction
   * sharing the same ID with the new instruction will be replaced.
   * 
   * @param instruction the instruction to add
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withInstruction(Instruction<?> instruction) {
    var dependents = new HashMap<>(this.dependents);
    var instructions = new ArrayList<>(this.instructions);

    instructions.remove(dependents.remove(instruction.id()));
    dependents.put(instruction.id(), instruction);
    instructions.add(instruction);

    return with(instructions, dependents);
  }

  /**
   * Derive a new container including the given instructions. Any instructions
   * sharing the same ID with a new instruction will be replaced.
   * 
   * @param instructions the instructions to add
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withInstructions(Collection<? extends Instruction<?>> instructions) {
    var dependents = new HashMap<>(this.dependents);
    var newInstructions = new ArrayList<>(this.instructions);

    for (Instruction<?> instruction : instructions) {
      newInstructions.remove(dependents.remove(instruction.id()));
      dependents.put(instruction.id(), instruction);
      newInstructions.add(instruction);
    }

    return with(newInstructions, dependents);
  }

  /**
   * Derive a new container, optionally performing a removal and addition of an
   * instruction. If an instruction of the given ID is present, it will be removed
   * from the resulting container, and will be passed to the given function in an
   * Optional, otherwise an empty Optional will be passed. If the function returns
   * an Optional containing an instruction, that instruction will be present in
   * the resulting container.
   * 
   * @param id          the ID of the instruction to replace
   * @param replacement a function accepting an optional over the existing
   *                    instruction and returning its replacement
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withInstruction(
      String id,
      Function<Optional<Instruction<?>>, Optional<Instruction<?>>> replacement) {
    var without = withoutInstruction(id);

    return replacement.apply(instruction(id)).map(without::withInstruction).orElse(without);
  }

  /**
   * Derive a new container, optionally modifying the contained instructions.
   * 
   * @param transformation a function accepting a stream of all the currently
   *                       present instructions, and returning a stream of all the
   *                       instructions to include in the derived container
   * @return The derived container, or optionally the receiving container if no
   *         change is made.
   */
  public T withInstructions(
      Function<Stream<Instruction<?>>, Stream<Instruction<?>>> transformation) {
    var instructions = instructions().collect(Collectors.toList());
    return withoutInstructions()
        .withInstructions(transformation.apply(instructions.stream()).collect(toList()));
  }
}
