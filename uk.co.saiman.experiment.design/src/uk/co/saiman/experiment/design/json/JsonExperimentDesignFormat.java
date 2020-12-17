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
import uk.co.saiman.experiment.design.ExperimentDesign;
import uk.co.saiman.experiment.design.ExperimentStepDesign;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;
import uk.co.saiman.state.json.JsonStateMapFormat;

/**
 * The .edef format, for loading experiment designs.
 * 
 * @author Elias N Vasylenko
 */
public class JsonExperimentDesignFormat implements TextFormat<ExperimentDesign> {
  public static final int VERSION = 1;

  public static final String FILE_EXTENSION = "edsn";
  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.experiment.design.v" + VERSION,
      VENDOR).withSuffix("json");

  private static final MapIndex<ExperimentId> ID = new MapIndex<>(
      "id",
      stringAccessor().map(ExperimentId::fromName, ExperimentId::name));

  private final MapIndex<List<ExperimentStepDesign>> substeps;

  private final JsonStateMapFormat stateMapFormat;

  public JsonExperimentDesignFormat(JsonExperimentStepDesignFormat stepDesignFormat) {
    this.stateMapFormat = stepDesignFormat.getStateMapFormat();
    this.substeps = stepDesignFormat.getSubstepsAccessor();
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
  public Payload<? extends ExperimentDesign> decodeString(String string) {
    return new Payload<>(loadDesign(stateMapFormat.decodeString(string).data));
  }

  public ExperimentDesign loadDesign(StateMap data) {
    return ExperimentDesign.define(data.get(ID)).withSubsteps(data.get(substeps));
  }

  @Override
  public String encodeString(Payload<? extends ExperimentDesign> payload) {
    return stateMapFormat.encodeString(new Payload<>(saveDesign(payload.data)));
  }

  public StateMap saveDesign(ExperimentDesign procedure) {
    return StateMap.empty().with(ID, procedure.id()).with(substeps, procedure.substeps().collect(toList()));
  }
}
