package uk.co.saiman.comms.copley.impl;

import java.util.Optional;

import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.VariableBank;
import uk.co.saiman.comms.copley.WritableVariable;

class WritableVariableImpl<T extends Enum<T>, U> extends VariableImpl<T, U>
		implements WritableVariable<T, U> {
	public WritableVariableImpl(
			CopleyCommsImpl<T> copleyCommsImpl,
			CopleyVariableID id,
			Class<U> variableClass,
			VariableBank bank) {
		super(copleyCommsImpl, id, variableClass, bank);
	}

	@Override
	public Optional<WritableVariable<T, U>> trySwitchBank(VariableBank bank) {
		return Optional.empty();
	}
}
