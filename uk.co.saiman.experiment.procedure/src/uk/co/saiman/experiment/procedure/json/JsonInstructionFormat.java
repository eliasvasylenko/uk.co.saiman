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

import static uk.co.saiman.data.format.MediaType.APPLICATION_TYPE;
import static uk.co.saiman.data.format.RegistrationTree.VENDOR;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.stream.Stream;

import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;
import uk.co.saiman.state.json.JsonStateMapFormat;

/**
 * The .inst format, for loading instructions.
 * 
 * @author Elias N Vasylenko
 */
public class JsonInstructionFormat implements TextFormat<Instruction> {
  public static final int VERSION = 1;

  public static final String FILE_EXTENSION = "inst"; // json instruction
  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.experiment.instruction.v" + VERSION,
      VENDOR).withSuffix("json");

  private static final MapIndex<ExperimentPath<Absolute>> PATH = new MapIndex<>(
      "path",
      stringAccessor().map(ExperimentPath::absoluteFromString, ExperimentPath::toString));
  private static final String EXECUTOR = "executor";
  private static final String VARIABLES = "variables";

  private final MapIndex<Executor> executor;

  private final JsonStateMapFormat stateMapFormat;

  public JsonInstructionFormat(ExecutorService executorService) {
    this(executorService, new JsonStateMapFormat());
  }

  public JsonInstructionFormat(
      ExecutorService conductorService,
      JsonStateMapFormat stateMapFormat) {
    this.stateMapFormat = stateMapFormat;

    this.executor = new MapIndex<>(
        EXECUTOR,
        stringAccessor().map(conductorService::getExecutor, conductorService::getId));
  }

  protected JsonStateMapFormat getStateMapFormat() {
    return stateMapFormat;
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
  public Payload<? extends Instruction> decodeString(String string) {
    return new Payload<>(loadInstruction(stateMapFormat.decodeString(string).data));
  }

  protected Instruction loadInstruction(StateMap data) {
    return new Instruction(data.get(PATH), data.get(VARIABLES).asMap(), data.get(executor));
  }

  @Override
  public String encodeString(Payload<? extends Instruction> payload) {
    return stateMapFormat.encodeString(new Payload<>(saveInstruction(payload.data)));
  }

  protected StateMap saveInstruction(Instruction step) {
    return StateMap
        .empty()
        .with(PATH, step.path())
        .with(VARIABLES, step.variableMap())
        .with(executor, step.executor());
  }
}
