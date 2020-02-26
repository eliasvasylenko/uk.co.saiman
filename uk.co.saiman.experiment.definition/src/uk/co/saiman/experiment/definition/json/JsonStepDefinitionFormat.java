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
package uk.co.saiman.experiment.definition.json;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.collection.StreamUtilities.throwingMerger;
import static uk.co.saiman.data.format.MediaType.APPLICATION_TYPE;
import static uk.co.saiman.data.format.RegistrationTree.VENDOR;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.List;
import java.util.stream.Stream;

import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.definition.Definition;
import uk.co.saiman.experiment.definition.ExecutionPlan;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.service.ExecutorService;
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
public class JsonStepDefinitionFormat implements TextFormat<StepDefinition> {
  public static final int VERSION = 1;

  public static final String FILE_EXTENSION = "jsd"; // json step definition
  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.step.definition.v" + VERSION,
      VENDOR).withSuffix("json");

  private static final MapIndex<ExperimentId> ID = new MapIndex<>(
      "id",
      stringAccessor().map(ExperimentId::fromName, ExperimentId::name));
  private static final MapIndex<ExecutionPlan> PLAN = new MapIndex<>(
      "plan",
      stringAccessor().map(ExecutionPlan::valueOf, ExecutionPlan::toString));
  private static final String EXECUTOR = "executor";
  private static final String VARIABLES = "variables";
  private static final String CHILDREN = "children";

  private final MapIndex<Executor> executor;

  private final JsonStateMapFormat stateMapFormat;

  public JsonStepDefinitionFormat(ExecutorService executorService) {
    this(executorService, new JsonStateMapFormat());
  }

  public JsonStepDefinitionFormat(
      ExecutorService conductorService,
      JsonStateMapFormat stateMapFormat) {
    this.stateMapFormat = stateMapFormat;

    this.executor = new MapIndex<>(
        EXECUTOR,
        stringAccessor().map(conductorService::getExecutor, conductorService::getId));
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
  public Payload<? extends StepDefinition> decodeString(String string) {
    return new Payload<>(loadStep(stateMapFormat.decodeString(string).data));
  }

  private <T extends Definition<?, ? extends T>> T loadSteps(T container, StateList data) {
    return data
        .stream()
        .map(State::asMap)
        .reduce(container, (p, s) -> p.withSubstep(loadStep(s)), throwingMerger());
  }

  private StepDefinition loadStep(StateMap data) {
    StepDefinition step = StepDefinition
        .define(data.get(ID), (Executor) data.get(executor))
        .withVariableMap(data.get(VARIABLES).asMap())
        .withPlan(data.get(PLAN));

    step = this.<StepDefinition>loadSteps(step, data.get(CHILDREN).asList());

    return step;
  }

  @Override
  public String encodeString(Payload<? extends StepDefinition> payload) {
    return stateMapFormat.encodeString(new Payload<>(saveStep(payload.data)));
  }

  protected StateList saveSteps(List<StepDefinition> steps) {
    return steps
        .stream()
        .reduce(StateList.empty(), (l, s) -> l.withAdded(saveStep(s)), throwingMerger());
  }

  protected StateMap saveStep(StepDefinition step) {
    return StateMap
        .empty()
        .with(ID, step.id())
        .with(VARIABLES, step.variableMap())
        .with(CHILDREN, saveSteps(step.substeps().collect(toList())))
        .with(PLAN, step.getPlan())
        .with(executor, step.executor());
  }
}