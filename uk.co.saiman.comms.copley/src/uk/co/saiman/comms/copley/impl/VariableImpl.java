package uk.co.saiman.comms.copley.impl;

import static uk.co.saiman.comms.copley.CopleyOperationID.COPY_VARIABLE;
import static uk.co.saiman.comms.copley.CopleyOperationID.GET_VARIABLE;
import static uk.co.saiman.comms.copley.CopleyOperationID.SET_VARIABLE;

import java.util.Optional;

import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.Variable;
import uk.co.saiman.comms.copley.VariableBank;
import uk.co.saiman.comms.copley.VariableIdentifier;

class VariableImpl<T extends Enum<T>, U> implements Variable<T, U> {
	private final CopleyCommsImpl<T> comms;
	private final CopleyVariableID id;
	private final Class<U> variableClass;
	private final VariableBank bank;

	public VariableImpl(
			CopleyCommsImpl<T> copleyCommsImpl,
			CopleyVariableID id,
			Class<U> variableClass,
			VariableBank bank) {
		this.comms = copleyCommsImpl;
		this.id = id;
		this.variableClass = variableClass;
		this.bank = bank;
	}

	@Override
	public CopleyCommsImpl<T> getComms() {
		return comms;
	}

	@Override
	public CopleyVariableID getID() {
		return id;
	}

	@Override
	public Class<U> getType() {
		return variableClass;
	}

	private VariableIdentifier getVariableID(CopleyVariableID variable, T axis, VariableBank bank) {
		comms.validateAxis(axis);

		VariableIdentifier identifier = new VariableIdentifier();
		identifier.axis = (byte) axis.ordinal();
		identifier.variableID = (byte) variable.getCode();
		identifier.bank = bank.getBit();
		return identifier;
	}

	private byte[] concat(byte[] left, byte[] right) {
		byte[] bytes = new byte[left.length + right.length];
		System.arraycopy(left, 0, bytes, 0, left.length);
		System.arraycopy(right, 0, bytes, left.length, right.length);
		return bytes;
	}

	@Override
	public U get(T axis) {
		VariableIdentifier variableID = getVariableID(id, axis, bank);

		byte[] outputBytes = this.comms.getConverters().getConverter(VariableIdentifier.class).toBytes(
				variableID);

		byte[] inputBytes = this.comms.executeCopleyCommand(GET_VARIABLE, outputBytes);

		return this.comms.getConverters().getConverter(variableClass).fromBytes(inputBytes);
	}

	public void set(T axis, U output) {
		VariableIdentifier variableID = getVariableID(id, axis, bank);

		byte[] outputBytes = concat(
				this.comms.getConverters().getConverter(VariableIdentifier.class).toBytes(variableID),
				this.comms.getConverters().getConverter(variableClass).toBytes(output));

		this.comms.executeCopleyCommand(SET_VARIABLE, outputBytes);
	}

	public void copyToBank(T axis) {
		VariableIdentifier variableID = getVariableID(id, axis, bank);

		byte[] outputBytes = this.comms.getConverters().getConverter(VariableIdentifier.class).toBytes(
				variableID);

		this.comms.executeCopleyCommand(COPY_VARIABLE, outputBytes);
	}

	@Override
	public VariableBank getBank() {
		return bank;
	}

	@Override
	public Optional<? extends Variable<T, U>> trySwitchBank(VariableBank bank) {
		return Optional.empty();
	}
}
