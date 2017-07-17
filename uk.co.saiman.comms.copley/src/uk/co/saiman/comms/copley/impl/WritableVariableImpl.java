package uk.co.saiman.comms.copley.impl;

import java.util.Optional;

import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.VariableBank;
import uk.co.saiman.comms.copley.WritableVariable;

class WritableVariableImpl<U> extends VariableImpl<U> implements WritableVariable<U> {
  public WritableVariableImpl(
      CopleyControllerImpl controller,
      CopleyVariableID id,
      Class<U> variableClass,
      VariableBank bank) {
    super(controller, id, variableClass, bank);
  }

  @Override
  public Optional<WritableVariable<U>> trySwitchBank(VariableBank bank) {
    return Optional.empty();
  }
}
