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
package uk.co.saiman.experiment.json;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.collection.StreamUtilities.throwingMerger;
import static uk.co.saiman.data.format.MediaType.APPLICATION_TYPE;
import static uk.co.saiman.data.format.RegistrationTree.VENDOR;
import static uk.co.saiman.state.Accessor.mapAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.List;
import java.util.stream.Stream;

import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.ConductorService;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.procedure.InstructionContainer;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.State;
import uk.co.saiman.state.StateList;
import uk.co.saiman.state.StateMap;
import uk.co.saiman.state.json.JsonStateMapFormat;

/**
 * A format for serializing and deserializing experiment procedures.
 * 
 * Because
 * 
 * @author Elias N Vasylenko
 *
 */
public class JsonProcedureFormat implements TextFormat<Procedure> {
  public static final int VERSION = 1;

  public static final String FILE_EXTENSION = "jep"; // json experiment procedure
  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.procedure.v" + VERSION,
      VENDOR).withSuffix("json");

  private static final MapIndex<String> ID = new MapIndex<>("id", stringAccessor());
  private static final String CONDUCTOR = "conductor";
  private static final String VARIABLES = "variables";
  private static final String CHILDREN = "children";

  private final MapIndex<Conductor<?>> conductor;
  private final MapIndex<Variables> variables;

  private final JsonStateMapFormat stateMapFormat;

  public JsonProcedureFormat(ConductorService conductorService) {
    this(conductorService, new JsonStateMapFormat());
  }

  public JsonProcedureFormat(ConductorService conductorService, JsonStateMapFormat stateMapFormat) {
    this.stateMapFormat = stateMapFormat;

    this.conductor = new MapIndex<>(
        CONDUCTOR,
        stringAccessor().map(conductorService::getConductor, conductorService::getId));

    this.variables = new MapIndex<>(VARIABLES, mapAccessor(Variables::new, Variables::state));
  }

  @Override
  public String getExtension() {
    return FILE_EXTENSION;
  }

  @Override
  public Stream<MediaType> getMediaTypes() {
    return Stream.of(MEDIA_TYPE);
  }

  @Override
  public Payload<? extends Procedure> decodeString(String string) {
    return new Payload<>(loadProcedure(stateMapFormat.decodeString(string).data));
  }

  public Procedure loadProcedure(StateMap data) {
    return loadInstructions(Procedure.define(data.get(ID)), data.get(CHILDREN).asList());
  }

  private <T extends InstructionContainer<T>> T loadInstructions(T container, StateList data) {
    return data
        .stream()
        .map(State::asMap)
        .reduce(container, (p, s) -> loadInstruction(p, s), throwingMerger());
  }

  private <T extends InstructionContainer<T>> T loadInstruction(T container, StateMap data) {
    var instruction = Instruction
        .define(data.get(ID), data.get(variables), (Conductor<?>) data.get(conductor));

    instruction = loadInstructions(instruction, data.get(CHILDREN).asList());

    return container.withInstruction(instruction);
  }

  @Override
  public String encodeString(Payload<? extends Procedure> payload) {
    return stateMapFormat.encodeString(new Payload<>(saveProcedure(payload.data)));
  }

  public StateMap saveProcedure(Procedure procedure) {
    return StateMap
        .empty()
        .with(ID, procedure.id())
        .with(CHILDREN, saveInstructions(procedure.independentInstructions().collect(toList())));
  }

  protected StateList saveInstructions(List<Instruction<?>> instructions) {
    return instructions
        .stream()
        .reduce(StateList.empty(), (l, s) -> l.withAdded(saveInstruction(s)), throwingMerger());
  }

  protected StateMap saveInstruction(Instruction<?> instruction) {
    return StateMap
        .empty()
        .with(ID, instruction.id())
        .with(variables, instruction.variables())
        .with(CHILDREN, saveInstructions(instruction.instructions().collect(toList())));
  }
}
