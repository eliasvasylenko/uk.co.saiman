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

import java.util.stream.Stream;

import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.definition.json.JsonExperimentDefinitionFormat;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.storage.service.StorageService;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;
import uk.co.saiman.state.json.JsonStateMapFormat;

public class JsonExperimentFormat implements TextFormat<Experiment> {
  public static final int VERSION = 1;

  public static final String FILE_EXTENSION = "jem"; // json experiment model
  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.experiment.v" + VERSION,
      VENDOR).withSuffix("json");

  private static final String STORAGE = "storage";

  private final MapIndex<StorageConfiguration<?>> storage;

  private final JsonStateMapFormat stateMapFormat;
  private final JsonExperimentDefinitionFormat definitionFormat;

  public JsonExperimentFormat(ExecutorService executorService, StorageService storageService) {
    this(executorService, storageService, new JsonStateMapFormat());
  }

  public JsonExperimentFormat(
      ExecutorService executorService,
      StorageService storageService,
      JsonStateMapFormat stateMapFormat) {
    this(
        storageService,
        stateMapFormat,
        new JsonExperimentDefinitionFormat(executorService, stateMapFormat));
  }

  public JsonExperimentFormat(
      StorageService storageService,
      JsonStateMapFormat stateMapFormat,
      JsonExperimentDefinitionFormat definitionFormat) {
    this.stateMapFormat = stateMapFormat;
    this.definitionFormat = definitionFormat;

    this.storage = new MapIndex<>(
        STORAGE,
        mapAccessor(
            s -> storageService.configureStorage(s),
            r -> storageService.deconfigureStorage(r)));
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
    return new Experiment(definitionFormat.loadProcedure(data), data.get(storage));
  }

  @Override
  public String encodeString(Payload<? extends Experiment> payload) {
    return stateMapFormat.encodeString(new Payload<>(saveExperiment(payload.data)));
  }

  protected StateMap saveExperiment(Experiment data) {
    return definitionFormat
        .saveProcedure(data.getDefinition())
        .with(storage, data.getStorageConfiguration());
  }
}
