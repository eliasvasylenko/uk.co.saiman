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
package uk.co.saiman.experiment.format;

import static uk.co.saiman.data.format.MediaType.APPLICATION_TYPE;
import static uk.co.saiman.data.format.RegistrationTree.VENDOR;
import static uk.co.saiman.state.Accessor.mapAccessor;

import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.definition.json.JsonExperimentDefinitionFormat;
import uk.co.saiman.experiment.definition.json.JsonStepDefinitionFormat;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.service.LocalEnvironmentService;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.storage.service.StorageService;
import uk.co.saiman.log.Log;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;
import uk.co.saiman.state.json.JsonStateMapFormat;

/**
 * The .exp format, for loading experiment step definitions.
 * 
 * @author Elias N Vasylenko
 */
public class JsonExperimentFormat implements TextFormat<Experiment> {
  public static final int VERSION = 1;

  public static final String FILE_EXTENSION = "exp";
  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.experiment.v" + VERSION,
      VENDOR).withSuffix("json");

  private static final String STORAGE = "storage";

  private final MapIndex<StorageConfiguration<?>> storage;

  private final JsonStateMapFormat stateMapFormat;
  private final ExecutorService executorService;
  private final Supplier<GlobalEnvironment> environment;
  private final LocalEnvironmentService localEnvironmentService;

  private final Log log;

  public JsonExperimentFormat(
      ExecutorService executorService,
      StorageService storageService,
      Supplier<GlobalEnvironment> environment,
      LocalEnvironmentService localEnvironmentService,
      Log log) {
    this(
        executorService,
        storageService,
        environment,
        localEnvironmentService,
        new JsonStateMapFormat(),
        log);
  }

  public JsonExperimentFormat(
      ExecutorService executorService,
      StorageService storageService,
      Supplier<GlobalEnvironment> environment,
      LocalEnvironmentService localEnvironmentService,
      JsonStateMapFormat stateMapFormat,
      Log log) {
    this.stateMapFormat = stateMapFormat;
    this.executorService = executorService;
    this.environment = environment;
    this.localEnvironmentService = localEnvironmentService;

    this.storage = new MapIndex<>(
        STORAGE,
        mapAccessor()
            .map(
                s -> storageService.configureStorage(s),
                r -> storageService.deconfigureStorage(r)));

    this.log = log;
  }

  public ExecutorService getExecutorService() {
    return executorService;
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
    var definitionFormat = new JsonExperimentDefinitionFormat(
        new JsonStepDefinitionFormat(executorService, stateMapFormat));
    var definition = definitionFormat.loadDefinition(data);
    return new Experiment(
        definition,
        data.get(storage),
        executorService,
        environment,
        localEnvironmentService,
        log.mapMessage(s -> definition.id() + ": " + s));
  }

  @Override
  public String encodeString(Payload<? extends Experiment> payload) {
    return stateMapFormat.encodeString(new Payload<>(saveExperiment(payload.data)));
  }

  protected StateMap saveExperiment(Experiment data) {
    var definitionFormat = new JsonExperimentDefinitionFormat(
        new JsonStepDefinitionFormat(data.getExecutorService(), stateMapFormat));
    return definitionFormat
        .saveDefinition(data.getDefinition())
        .with(storage, data.getStorageConfiguration());
  }
}
