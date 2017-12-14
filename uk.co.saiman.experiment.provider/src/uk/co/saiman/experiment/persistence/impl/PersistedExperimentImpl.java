package uk.co.saiman.experiment.persistence.impl;

import java.io.IOException;
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
    return state.forMap(CONFIGURATION_KEY);
  }

  @Override
  public void setId(String id) throws IOException {
    state.forString(ID_KEY).set(id);
  }

  @Override
  public Stream<PersistedExperiment> getChildren() {
    return state.forMapList(NODE_KEY).stream().map(PersistedExperimentImpl::new);
  }

  @Override
  public PersistedExperiment addChild(int index, String typeId, PersistedState initialState) {
    PersistedState newState = state.forMapList(NODE_KEY).add(index);
    if (initialState != null) {
      newState.copyState(initialState);
    }
    return new PersistedExperimentImpl(newState, null, typeId);
  }

  @Override
  public void removeChild(PersistedExperiment child) {
    state.forMapList(NODE_KEY).remove(((PersistedExperimentImpl) child).state);
  }
}
