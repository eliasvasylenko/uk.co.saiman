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
package uk.co.saiman.experiment.definition.json;

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
import uk.co.saiman.experiment.definition.ExperimentDefinition;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;
import uk.co.saiman.state.json.JsonStateMapFormat;

/**
 * The .edef format, for loading experiment definitions.
 * 
 * @author Elias N Vasylenko
 */
public class JsonExperimentDefinitionFormat implements TextFormat<ExperimentDefinition> {
  public static final int VERSION = 1;

  public static final String FILE_EXTENSION = "edef";
  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.experiment.definition.v" + VERSION,
      VENDOR).withSuffix("json");

  private static final MapIndex<ExperimentId> ID = new MapIndex<>(
      "id",
      stringAccessor().map(ExperimentId::fromName, ExperimentId::name));

  private final MapIndex<List<StepDefinition>> substeps;

  private final JsonStateMapFormat stateMapFormat;

  public JsonExperimentDefinitionFormat(JsonStepDefinitionFormat stepDefinitionFormat) {
    this.stateMapFormat = stepDefinitionFormat.getStateMapFormat();
    this.substeps = stepDefinitionFormat.getSubstepsAccessor();
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
  public Payload<? extends ExperimentDefinition> decodeString(String string) {
    return new Payload<>(loadDefinition(stateMapFormat.decodeString(string).data));
  }

  public ExperimentDefinition loadDefinition(StateMap data) {
    return ExperimentDefinition.define(data.get(ID)).withSubsteps(data.get(substeps));
  }

  @Override
  public String encodeString(Payload<? extends ExperimentDefinition> payload) {
    return stateMapFormat.encodeString(new Payload<>(saveDefinition(payload.data)));
  }

  public StateMap saveDefinition(ExperimentDefinition procedure) {
    return StateMap
        .empty()
        .with(ID, procedure.id())
        .with(substeps, procedure.substeps().collect(toList()));
  }
}
