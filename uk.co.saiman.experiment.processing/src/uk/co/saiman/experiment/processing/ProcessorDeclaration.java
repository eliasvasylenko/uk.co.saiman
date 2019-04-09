package uk.co.saiman.experiment.processing;

import static uk.co.saiman.state.Accessor.mapAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;

public class ProcessorDeclaration {
  private static final MapIndex<String> PROCESSOR_ID = new MapIndex<>(
      "uk.co.saiman.experiment.processor.id",
      stringAccessor());

  public static final Variable<ProcessorDeclaration> PROCESSOR_VARIABLE = new Variable<>(
      "uk.co.saiman.processor",
      mapAccessor(ProcessorDeclaration::fromState, ProcessorDeclaration::toState));

  private final String id;
  private final StateMap state;

  public ProcessorDeclaration(String id, StateMap state) {
    this.id = id;
    this.state = state;
  }

  public static ProcessorDeclaration fromState(StateMap state) {
    String id = state.get(PROCESSOR_ID);
    return new ProcessorDeclaration(id, state.remove(PROCESSOR_ID));
  }

  public static StateMap toState(ProcessorDeclaration declaration) {
    return declaration.state().with(PROCESSOR_ID, declaration.id());
  }

  public String id() {
    return id;
  }

  public StateMap state() {
    return state;
  }
}
