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
package uk.co.saiman.experiment.persistence.impl;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import uk.co.saiman.experiment.impl.PersistedExperiment;
import uk.co.saiman.experiment.persistence.PersistedState;

public class PersistedExperimentImpl implements PersistedExperiment {
  private static final String ID_KEY = "id";
  private static final String TYPE_ID_KEY = "type";
  private static final String CONFIGURATION_KEY = "configuration";
  private static final String NODE_KEY = "nodes";

  private final PersistedState state;

  public PersistedExperimentImpl(PersistedState state, String id, String typeId) {
    this.state = state;
    state.forString(ID_KEY).set(id);
    state.forString(TYPE_ID_KEY).set(typeId);
  }

  public PersistedExperimentImpl(PersistedState state) {
    this.state = state;
  }

  @Override
  public String getId() {
    return state.forString(ID_KEY).get();
  }

  @Override
  public String getTypeId() {
    return state.forString(TYPE_ID_KEY).get();
  }

  @Override
  public PersistedState getPersistedState() {
    return state.getMap(CONFIGURATION_KEY);
  }

  @Override
  public void setId(String id) throws IOException {
    state.forString(ID_KEY).set(id);
  }

  @Override
  public Stream<PersistedExperimentImpl> getChildren() {
    return state.getMapList(NODE_KEY).stream().map(PersistedExperimentImpl::new);
  }

  @Override
  public PersistedExperiment addChild(
      String id,
      String typeId,
      PersistedState configuration,
      int index) throws IOException {
    PersistedState newState = state.getMapList(NODE_KEY).add(index);

    if (configuration == null)
      configuration = newState.getMap(CONFIGURATION_KEY);
    else
      newState.setMap(CONFIGURATION_KEY, configuration);

    return new PersistedExperimentImpl(newState, id, typeId);
  }

  @Override
  public void removeChild(String id, String typeId) {
    getChildren()
        .filter(c -> c.getId().equals(id) && c.getTypeId().equals(typeId))
        .findAny()
        .ifPresent(c -> state.getMapList(NODE_KEY).remove(c.state));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof PersistedExperimentImpl))
      return false;

    PersistedExperimentImpl that = (PersistedExperimentImpl) obj;

    return Objects.equals(this.state, that.state);
  }

  @Override
  public int hashCode() {
    return state.hashCode();
  }
}
