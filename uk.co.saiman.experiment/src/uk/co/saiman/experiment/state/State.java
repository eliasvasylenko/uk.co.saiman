package uk.co.saiman.experiment.state;

import static uk.co.saiman.experiment.state.StateKind.LIST;
import static uk.co.saiman.experiment.state.StateKind.MAP;
import static uk.co.saiman.experiment.state.StateKind.PROPERTY;

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
