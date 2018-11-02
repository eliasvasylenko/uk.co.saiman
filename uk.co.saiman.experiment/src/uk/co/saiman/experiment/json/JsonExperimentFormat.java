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
package uk.co.saiman.experiment.json;

import static uk.co.saiman.data.format.MediaType.APPLICATION_TYPE;
import static uk.co.saiman.data.format.RegistrationTree.VENDOR;
import static uk.co.saiman.experiment.state.Accessor.stringAccessor;
import static uk.co.saiman.experiment.state.StateList.toStateList;

import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.Procedure;
import uk.co.saiman.experiment.ResultStore;
import uk.co.saiman.experiment.service.ProcedureService;
import uk.co.saiman.experiment.service.ResultStorageService;
import uk.co.saiman.experiment.state.Accessor.PropertyAccessor;
import uk.co.saiman.experiment.state.State;
import uk.co.saiman.experiment.state.StateMap;

@Component
public class JsonExperimentFormat implements TextFormat<Experiment> {
  public static final int VERSION = 1;

  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.experiment.v" + VERSION,
      VENDOR).withSuffix("json");

  private static final PropertyAccessor<String> ID = stringAccessor("id");
  private static final String CONFIGURATION = "configuration";
  private static final String CHILDREN = "children";

  private final JsonStateMapFormat stateMapFormat = new JsonStateMapFormat();

  private final PropertyAccessor<Procedure<?, ?>> procedure;

  private final PropertyAccessor<ResultStore> resultStorage;

  @Activate
  public JsonExperimentFormat(
      @Reference ProcedureService procedureService,
      @Reference ResultStorageService resultStorageService) {
    this.procedure = stringAccessor("procedure")
        .map(procedureService::getProcedure, procedureService::getId);

    this.resultStorage = stringAccessor("resultStorage")
        .map(
            s -> resultStorageService.getResultStorageStrategy(s).configureStore(StateMap.empty()),
            r -> "unknown");
  }

  @Override
  public String getExtension() {
    return "esm";
  }

  @Override
  public Stream<MediaType> getMediaTypes() {
    return Stream.of(MEDIA_TYPE);
  }

  @Override
  public Payload<? extends Experiment> decodeString(String string) {
    return new Payload<>(loadExperiment(stateMapFormat.decodeString(string).data));
  }

  private Experiment loadExperiment(StateMap data) {
    return loadExperimentNode(new Experiment(data.get(ID), data.get(resultStorage)), data);
  }

  private <T extends ExperimentNode<?, ?>> T loadExperimentNode(T experimentNode, StateMap data) {
    data
        .get(CHILDREN)
        .asList()
        .stream()
        .map(State::asMap)
        .forEach(
            child -> experimentNode
                .attach(
                    loadExperimentNode(
                        new ExperimentNode<>(
                            child.get(procedure),
                            child.get(ID),
                            child.get(CONFIGURATION).asMap()),
                        child)));
    return experimentNode;
  }

  @Override
  public String encodeString(Payload<? extends Experiment> payload) {
    return stateMapFormat.encodeString(saveExperiment(payload.data));
  }

  private Payload<? extends StateMap> saveExperiment(Experiment data) {
    StateMap stateMap = saveExperimentNode(data);
    return new Payload<>(stateMap);
  }

  private StateMap saveExperimentNode(ExperimentNode<?, ?> node) {
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
