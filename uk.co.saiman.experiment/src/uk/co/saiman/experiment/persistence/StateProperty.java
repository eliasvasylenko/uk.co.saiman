package uk.co.saiman.experiment.persistence;

import static uk.co.saiman.experiment.persistence.StateKind.PROPERTY;

public interface StateProperty extends State {
  String getValue();

  @Override
  default StateKind getKind() {
    return PROPERTY;
  }

  static StateProperty stateProperty(String value) {
    return () -> value;
  }
}
