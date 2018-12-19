/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.storage.filesystem;

import static uk.co.saiman.data.format.MediaType.APPLICATION_TYPE;
import static uk.co.saiman.data.format.RegistrationTree.VENDOR;
import static uk.co.saiman.experiment.state.Accessor.mapAccessor;
import static uk.co.saiman.experiment.state.Accessor.stringAccessor;
import static uk.co.saiman.experiment.state.StateList.toStateList;

import java.util.stream.Stream;

import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentStep;
import uk.co.saiman.experiment.Procedure;
import uk.co.saiman.experiment.scheduling.SchedulingStrategy;
import uk.co.saiman.experiment.service.ProcedureService;
import uk.co.saiman.experiment.service.StorageService;
import uk.co.saiman.experiment.state.Accessor.MapAccessor;
import uk.co.saiman.experiment.state.Accessor.PropertyAccessor;
import uk.co.saiman.experiment.state.State;
import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.experiment.state.json.JsonStateMapFormat;
import uk.co.saiman.experiment.storage.StorageConfiguration;

public class JsonExperimentFormat implements TextFormat<Experiment> {
  public static final int VERSION = 1;

  public static final String FILE_EXTENSION = "esm";
  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.experiment.v" + VERSION,
      VENDOR).withSuffix("json");

  private static final PropertyAccessor<String> ID = stringAccessor("id");
  private static final String STORAGE = "storage";
  private static final String PROCEDURE = "procedure";
  private static final String CONFIGURATION = "configuration";
  private static final String CHILDREN = "children";

  private final PropertyAccessor<Procedure<?>> procedure;
  private final MapAccessor<StorageConfiguration<?>> storage;
  private final SchedulingStrategy schedulingStrategy;

  private final JsonStateMapFormat stateMapFormat = new JsonStateMapFormat();

  public JsonExperimentFormat(
      ProcedureService procedureService,
      StorageService storageService,
      SchedulingStrategy schedulingStrategy) {
    this.procedure = stringAccessor(PROCEDURE)
        .map(procedureService::getProcedure, procedureService::getId);
    this.storage = mapAccessor(
        STORAGE,
        s -> storageService.configureStorage(s),
        r -> storageService.deconfigureStorage(r));
    this.schedulingStrategy = schedulingStrategy;
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
  public Payload<? extends Experiment> decodeString(String string) {
    return new Payload<>(loadExperiment(stateMapFormat.decodeString(string).data));
  }

  protected Experiment loadExperiment(StateMap data) {
    return loadExperimentNode(
        new Experiment(data.get(ID), data.get(storage), schedulingStrategy),
        data);
  }

  protected <T extends ExperimentStep<?>> T loadExperimentNode(T experimentNode, StateMap data) {
    data
        .get(CHILDREN)
        .asList()
        .stream()
        .map(State::asMap)
        .forEach(
            child -> experimentNode
                .attach(
                    loadExperimentNode(
                        new ExperimentStep<>(
                            (Procedure<?>) child.get(procedure),
                            child.get(ID),
                            child.get(CONFIGURATION).asMap()),
                        child)));
    return experimentNode;
  }

  @Override
  public String encodeString(Payload<? extends Experiment> payload) {
    return stateMapFormat.encodeString(new Payload<>(saveExperiment(payload.data)));
  }

  protected StateMap saveExperiment(Experiment data) {
    return saveExperimentNode(data).with(storage, data.getStorageConfiguration());
  }

  protected StateMap saveExperimentNode(ExperimentStep<?> node) {
    return StateMap
        .empty()
        .with(ID, node.getId())
        .with(CONFIGURATION, node.getStateMap())
        .with(
            CHILDREN,
            node
                .getChildren()
                .map(child -> saveExperimentNode(child).with(procedure, child.getProcedure()))
                .collect(toStateList()));
  }
}
