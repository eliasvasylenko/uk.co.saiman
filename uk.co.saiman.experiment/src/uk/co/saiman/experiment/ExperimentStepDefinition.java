package uk.co.saiman.experiment;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.ExperimentDefinition.listWithElement;
import static uk.co.saiman.experiment.ExperimentDefinition.listWithoutElement;
import static uk.co.saiman.experiment.state.StateMap.empty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.experiment.state.StateMap;

public class ExperimentStepDefinition<S> {
  private final String id;
  private final Procedure<S, ?> procedure;
  private final StateMap state;

  private final Map<Capability<?>, List<ExperimentComponentDefinition>> capabilities;
  private final Map<String, ExperimentComponentDefinition> components;

  public ExperimentStepDefinition(String id, Procedure<S, ?> procedure) {
    this(id, procedure, empty(), emptyMap(), emptyMap());
  }

  private ExperimentStepDefinition(
      String id,
      Procedure<S, ?> procedure,
      StateMap state,
      Map<Capability<?>, List<ExperimentComponentDefinition>> capabilities,
      Map<String, ExperimentComponentDefinition> components) {
    this.id = id;
    this.procedure = procedure;
    this.state = state;
    this.capabilities = capabilities;
    this.components = components;
  }

  public String id() {
    return id;
  }

  public Procedure<S, ?> procedure() {
    return procedure;
  }

  public StateMap state() {
    return state;
  }

  public ExperimentStepDefinition<S> withState(StateMap state) {
    return new ExperimentStepDefinition<>(id, procedure, state, capabilities, components);
  }

  public ExperimentStepDefinition<S> withState(Function<? super S, ? extends S> mapping) {
    return new ExperimentStepDefinition<>(id, procedure, state, capabilities, components);
  }

  private List<ExperimentComponentDefinition> getComponents(Capability<?> capability) {
    return capabilities.getOrDefault(capability, emptyList());
  }

  public Stream<ExperimentComponentDefinition> components(Capability<?> capability) {
    return getComponents(capability).stream();
  }

  public Optional<ExperimentComponentDefinition> component(String id) {
    return Optional.ofNullable(components.get(id));
  }

  public ExperimentStepDefinition<S> withComponent(
      Capability<?> capability,
      ExperimentStepDefinition<?> step) {
    return withComponent(capability, getComponents(capability).size(), step);
  }

  public ExperimentStepDefinition<S> withComponent(
      Capability<?> capability,
      int index,
      ExperimentStepDefinition<?> step) {
    return withComponents(
        capability,
        listWithElement(
            getComponents(capability)
                .stream()
                .map(ExperimentComponentDefinition::step)
                .collect(toList()),
            index,
            step));
  }

  public ExperimentStepDefinition<S> withComponents(
      Capability<?> capability,
      Collection<ExperimentStepDefinition<?>> steps) {
    var capabilities = new HashMap<>(this.capabilities);
    var components = new HashMap<>(this.components);

    getComponents(capability).forEach(component -> components.remove(component.step().id()));

    if (steps.isEmpty()) {
      capabilities.remove(capability);

    } else {
      var capabilityComponents = new ArrayList<ExperimentComponentDefinition>(steps.size());

      int i = 0;
      for (ExperimentStepDefinition<?> step : steps) {
        var component = this.components.get(step.id());
        if (component != null && component.capability() != capability) {
          throw new ExperimentException(
              format("Experiment step id %s must be unique within container %s", step.id(), id));
        }

        component = new ExperimentComponentDefinition(capability, i++, step);

        capabilityComponents.add(component);
        components.put(component.step().id(), component);
      }
      capabilities.put(capability, capabilityComponents);
    }

    return new ExperimentStepDefinition<>(id, procedure, state, capabilities, components);
  }

  public ExperimentStepDefinition<S> withoutComponent(String id) {
    return component(id)
        .map(component -> withoutComponent(component.capability(), component.index()))
        .orElse(this);
  }

  public ExperimentStepDefinition<S> withoutComponent(Capability<?> capability, int index) {
    return withComponents(
        capability,
        listWithoutElement(
            getComponents(capability)
                .stream()
                .map(ExperimentComponentDefinition::step)
                .collect(toList()),
            index));
  }

  public ExperimentStepDefinition<S> withoutComponents(Capability<?> capability) {
    return withComponents(capability, emptyList());
  }
}
