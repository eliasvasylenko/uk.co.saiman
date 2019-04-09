package uk.co.saiman.experiment.processing;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.state.Accessor.listAccessor;
import static uk.co.saiman.state.StateList.toStateList;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.state.State;
import uk.co.saiman.state.StateList;

public class ProcessingDeclaration {
  public static final Variable<ProcessingDeclaration> PROCESSING_VARIABLE = new Variable<>(
      "uk.co.saiman.processing",
      listAccessor(ProcessingDeclaration::fromState, ProcessingDeclaration::toState));

  private final List<ProcessorDeclaration> processors;

  public ProcessingDeclaration(Collection<? extends ProcessorDeclaration> processors) {
    this.processors = List.copyOf(processors);
  }

  public static ProcessingDeclaration fromState(StateList state) {
    return state
        .stream()
        .map(State::asMap)
        .map(ProcessorDeclaration::fromState)
        .collect(toProcessingDeclaration());
  }

  public static StateList toState(ProcessingDeclaration declaration) {
    return declaration.processors().map(ProcessorDeclaration::toState).collect(toStateList());
  }

  public Stream<ProcessorDeclaration> processors() {
    return processors.stream();
  }

  public static Collector<ProcessorDeclaration, ?, ProcessingDeclaration> toProcessingDeclaration() {
    return collectingAndThen(toList(), ProcessingDeclaration::new);
  }
}
