package uk.co.saiman.comms.copley.impl;

import java.util.Optional;

import uk.co.saiman.comms.copley.BankedVariable;
import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.VariableBank;
import uk.co.saiman.comms.copley.WritableVariable;

class BankedVariableImpl<T extends Enum<T>, U> extends WritableVariableImpl<T, U>
		implements BankedVariable<T, U> {
	public BankedVariableImpl(
			CopleyCommsImpl<T> copleyCommsImpl,
			CopleyVariableID id,
			Class<U> variableClass) {
		super(copleyCommsImpl, id, variableClass, VariableBank.ACTIVE);
	}

	@Override
	public Optional<WritableVariable<T, U>> trySwitchBank(VariableBank bank) {
		return Optional.of(switchBank(bank));
	}

	@Override
	public BankedVariable<T, U> switchBank(VariableBank bank) {
		return new BankedVariableImpl<>(getComms(), getID(), getType());
	}
}
