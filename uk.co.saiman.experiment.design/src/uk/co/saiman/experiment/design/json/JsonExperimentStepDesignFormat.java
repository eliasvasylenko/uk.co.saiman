/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.experiment.design.json;

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
import uk.co.saiman.experiment.design.ExecutionPlan;
import uk.co.saiman.experiment.design.ExperimentStepDesign;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.state.Accessor;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;
import uk.co.saiman.state.json.JsonStateMapFormat;

/**
 * The .sdef format, for loading experiment step designs.
 * 
 * @author Elias N Vasylenko
 */
public class JsonExperimentStepDesignFormat implements TextFormat<ExperimentStepDesign> {
  public static final int VERSION = 1;

  public static final String FILE_EXTENSION = "sdsn"; // json step design
  public static final MediaType MEDIA_TYPE = new MediaType(APPLICATION_TYPE, "saiman.step.design.v" + VERSION, VENDOR)
      .withSuffix("json");

  private static final MapIndex<ExperimentId> ID = new MapIndex<>(
      "id",
      stringAccessor().map(ExperimentId::fromName, ExperimentId::name));
  private static final MapIndex<ExecutionPlan> PLAN = new MapIndex<>(
      "plan",
      stringAccessor().map(ExecutionPlan::valueOf, ExecutionPlan::toString));
  private static final String EXECUTOR = "executor";
  private static final String VARIABLES = "variables";
  private static final String SUBSTEPS = "substeps";

  private final MapIndex<Executor> executor;
  private final MapIndex<List<ExperimentStepDesign>> substeps;

  private final JsonStateMapFormat stateMapFormat;

  public JsonExperimentStepDesignFormat(ExecutorService executorService) {
    this(executorService, new JsonStateMapFormat());
  }

  public JsonExperimentStepDesignFormat(ExecutorService conductorService, JsonStateMapFormat stateMapFormat) {
    this.stateMapFormat = stateMapFormat;

    this.executor = new MapIndex<>(
        EXECUTOR,
        stringAccessor().map(conductorService::getExecutor, conductorService::getId));
    this.substeps = new MapIndex<>(
        SUBSTEPS,
        Accessor.mapAccessor().map(this::loadStep, this::saveStep).toListAccessor());
  }

  MapIndex<List<ExperimentStepDesign>> getSubstepsAccessor() {
    return substeps;
  }

  protected JsonStateMapFormat getStateMapFormat() {
    return stateMapFormat;
  }

  @Override
  public Stream<String> getExtensions() {
    return Stream.of(FILE_EXTENSION);
  }

  @Override
  public Stream<MediaType> getMediaTypes() {
    return Stream.of(MEDIA_TYPE);
  }

  @Override
  public Payload<? extends ExperimentStepDesign> decodeString(String string) {
    return new Payload<>(loadStep(stateMapFormat.decodeString(string).data));
  }

  protected ExperimentStepDesign loadStep(StateMap data) {
    ExperimentStepDesign step = ExperimentStepDesign
        .define(data.get(ID))
        .withVariableMap(data.get(VARIABLES).asMap())
        .withSubsteps(data.get(substeps))
        .withPlan(data.get(PLAN));

    return data.getOptional(executor).map(step::withExecutor).orElse(step);
  }

  @Override
  public String encodeString(Payload<? extends ExperimentStepDesign> payload) {
    return stateMapFormat.encodeString(new Payload<>(saveStep(payload.data)));
  }

  protected StateMap saveStep(ExperimentStepDesign step) {
    StateMap state = StateMap
        .empty()
        .with(ID, step.id())
        .with(VARIABLES, step.variableMap())
        .with(substeps, step.substeps().collect(toList()))
        .with(PLAN, step.plan());

    return step.executor().map(e -> state.with(executor, e)).orElse(state);
  }
}
