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
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.persistence.json;

import java.io.IOException;
import java.nio.file.Path;

import uk.co.saiman.data.DataException;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.resource.PathResource;
import uk.co.saiman.data.resource.Resource;
import uk.co.saiman.experiment.persistence.PersistedState;

public class JsonPersistedStateDocument {
  private final JsonPersistedStateFormat format;
  private Resource resource;
  private final PersistedState persistedState;

  public JsonPersistedStateDocument(Resource resource) throws IOException {
    this(resource, new PersistedState());
  }

  public JsonPersistedStateDocument(Resource resource, PersistedState persistedState)
      throws IOException {
    this.format = new JsonPersistedStateFormat();
    this.resource = resource;
    this.persistedState = persistedState;
    persistedState.changes().observe(m -> save());
  }

  public JsonPersistedStateDocument setPath(Path path) {
    return setResource(new PathResource(path));
  }

  public Resource getResource() {
    return resource;
  }

  public JsonPersistedStateDocument setResource(Resource resource) {
    this.resource = resource;
    save();
    return this;
  }

  public PersistedState getPersistedState() {
    return persistedState;
  }

  private JsonPersistedStateDocument save() {
    try {
      format.save(resource.write(), new Payload<>(this.persistedState));
      return this;
    } catch (IOException e) {
      throw new DataException("Failed to save JSON persisted state", e);
    }
  }

  public JsonPersistedStateDocument load() {
    try {
      format.load(resource.read(), persistedState);
      return this;
    } catch (IOException e) {
      throw new DataException("Failed to load JSON persisted state", e);
    }
  }
}
