package uk.co.saiman.experiment.persistence;

import static uk.co.saiman.experiment.persistence.StateKind.LIST;
import static uk.co.saiman.experiment.persistence.StateKind.MAP;
import static uk.co.saiman.experiment.persistence.StateKind.PROPERTY;

public interface State {
  StateKind getKind();

  /*
   * TODO with amber generic enums, refactor to only need #as method.
   */

  default State as(StateKind kind) {
    if (getKind() != kind) {
      throw new UnexpectedStateKindException();
    }
    return this;
  }

  default StateProperty asProperty() {
    if (getKind() != PROPERTY) {
      throw new UnexpectedStateKindException();
    }
    return (StateProperty) this;
  }

  default StateMap asMap() {
    if (getKind() != MAP) {
      throw new UnexpectedStateKindException();
    }
    return (StateMap) this;
  }

  default StateList asList() {
    if (getKind() != LIST) {
      throw new UnexpectedStateKindException();
    }
    return (StateList) this;
  }
}
