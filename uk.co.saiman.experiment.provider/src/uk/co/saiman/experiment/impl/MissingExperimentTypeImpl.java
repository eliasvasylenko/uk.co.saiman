package uk.co.saiman.experiment.impl;

import static java.util.Collections.unmodifiableMap;

import java.util.HashMap;
import java.util.Map;

import uk.co.saiman.experiment.ExperimentConfigurationContext;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentExecutionContext;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.MissingExperimentType;

public class MissingExperimentTypeImpl implements MissingExperimentType {
  private final ExperimentWorkspaceImpl workspace;
  private final String id;

  protected MissingExperimentTypeImpl(ExperimentWorkspaceImpl workspace, String id) {
    this.workspace = workspace;
    this.id = id;
  }

  @Override
  public String getName() {
    return workspace.getText().missingExperimentType(id).toString();
  }

  public String getMissingTypeID() {
    return id;
  }

  @Override
  public Map<String, String> createState(
      ExperimentConfigurationContext<Map<String, String>> context) {
    Map<String, String> state = new HashMap<>();

    context.persistedState().putString(getID(), getMissingTypeID());
    context.persistedState().getStrings().forEach(
        string -> state.put(string, context.persistedState().getString(string).get()));

    return unmodifiableMap(state);
  }

  @Override
  public void execute(ExperimentExecutionContext<Map<String, String>> context) {
    throw new ExperimentException(workspace.getText().cannotExecuteMissingExperimentType(id));
  }

  @Override
  public boolean mayComeAfter(ExperimentNode<?, ?> parentNode) {
    return true;
  }

  @Override
  public boolean mayComeBefore(
      ExperimentNode<?, ?> penultimateDescendantNode,
      ExperimentType<?> descendantNodeType) {
    return true;
  }
}
