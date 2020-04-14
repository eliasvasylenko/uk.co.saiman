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
package uk.co.saiman.experiment.procedure.json;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.data.format.MediaType.APPLICATION_TYPE;
import static uk.co.saiman.data.format.RegistrationTree.VENDOR;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.List;
import java.util.stream.Stream;

import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.state.Accessor;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;
import uk.co.saiman.state.json.JsonStateMapFormat;

/**
 * The .proc format, for loading procedures.
 * 
 * @author Elias N Vasylenko
 */
public class JsonProcedureFormat implements TextFormat<Procedure> {
  public static final int VERSION = 1;

  public static final String FILE_EXTENSION = "proc"; // json experiment
  // procedure
  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.experiment.procedure.v" + VERSION,
      VENDOR).withSuffix("json");

  private static final MapIndex<ExperimentId> ID = new MapIndex<>(
      "id",
      stringAccessor().map(ExperimentId::fromName, ExperimentId::name));
  private static final String INSTRUCTIONS = "instructions";

  private final MapIndex<List<Instruction>> children;

  private final JsonStateMapFormat stateMapFormat;

  private final Environment environment;

  public JsonProcedureFormat(
      JsonInstructionFormat instructionFormat,
      Environment environment) {
    this.environment = environment;
    this.stateMapFormat = instructionFormat.getStateMapFormat();
    this.children = new MapIndex<>(
        INSTRUCTIONS,
        Accessor
            .mapAccessor()
            .map(instructionFormat::loadInstruction, instructionFormat::saveInstruction)
            .toListAccessor());
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
    return new Procedure(data.get(ID), data.get(children), environment);
  }

  @Override
  public String encodeString(Payload<? extends Procedure> payload) {
    return stateMapFormat.encodeString(new Payload<>(saveDefinition(payload.data)));
  }

  public StateMap saveDefinition(Procedure procedure) {
    return StateMap
        .empty()
        .with(ID, procedure.id())
        .with(children, procedure.instructions().collect(toList()));
  }
}
