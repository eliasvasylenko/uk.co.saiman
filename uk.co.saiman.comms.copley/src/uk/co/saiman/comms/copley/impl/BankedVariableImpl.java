package uk.co.saiman.comms.copley.impl;

import java.util.Optional;

import uk.co.saiman.comms.copley.BankedVariable;
import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.VariableBank;
import uk.co.saiman.comms.copley.WritableVariable;

class BankedVariableImpl<U> extends WritableVariableImpl<U> implements BankedVariable<U> {
  public BankedVariableImpl(
      CopleyControllerImpl controller,
      CopleyVariableID id,
      Class<U> variableClass) {
    this(controller, id, variableClass, VariableBank.ACTIVE);
  }

  private BankedVariableImpl(
      CopleyControllerImpl controller,
      CopleyVariableID id,
      Class<U> variableClass,
      VariableBank bank) {
    super(controller, id, variableClass, bank);
  }

  @Override
  public Optional<WritableVariable<U>> trySwitchBank(VariableBank bank) {
    return Optional.of(switchBank(bank));
  }

  @Override
  public BankedVariable<U> switchBank(VariableBank bank) {
    return new BankedVariableImpl<>(getController(), getID(), getType(), bank);
  }
}
