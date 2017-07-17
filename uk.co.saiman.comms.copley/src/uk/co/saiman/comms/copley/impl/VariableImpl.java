package uk.co.saiman.comms.copley.impl;

import static uk.co.saiman.comms.copley.CopleyOperationID.COPY_VARIABLE;
import static uk.co.saiman.comms.copley.CopleyOperationID.GET_VARIABLE;
import static uk.co.saiman.comms.copley.CopleyOperationID.SET_VARIABLE;

import java.util.Optional;

import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.MotorAxis;
import uk.co.saiman.comms.copley.Variable;
import uk.co.saiman.comms.copley.VariableBank;
import uk.co.saiman.comms.copley.VariableIdentifier;

class VariableImpl<U> implements Variable<U> {
  private final CopleyControllerImpl controller;
  private final CopleyVariableID id;
  private final Class<U> variableClass;
  private final VariableBank bank;

  public VariableImpl(
      CopleyControllerImpl controller,
      CopleyVariableID id,
      Class<U> variableClass,
      VariableBank bank) {
    this.controller = controller;
    this.id = id;
    this.variableClass = variableClass;
    this.bank = bank;
  }

  @Override
  public CopleyControllerImpl getController() {
    return controller;
  }

  @Override
  public CopleyVariableID getID() {
    return id;
  }

  @Override
  public Class<U> getType() {
    return variableClass;
  }

  private VariableIdentifier getVariableID(
      CopleyVariableID variable,
      MotorAxis axis,
      VariableBank bank) {
    VariableIdentifier identifier = new VariableIdentifier();
    identifier.axis = (byte) axis.axisNumber();
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
  public U get(MotorAxis axis) {
    VariableIdentifier variableID = getVariableID(id, axis, bank);

    byte[] outputBytes = controller.getConverters().getConverter(VariableIdentifier.class).toBytes(
        variableID);

    byte[] inputBytes = controller.executeCopleyCommand(GET_VARIABLE, outputBytes);

    return controller.getConverters().getConverter(variableClass).fromBytes(inputBytes);
  }

  public void set(MotorAxis axis, U output) {
    VariableIdentifier variableID = getVariableID(id, axis, bank);

    byte[] outputBytes = concat(
        controller.getConverters().getConverter(VariableIdentifier.class).toBytes(variableID),
        controller.getConverters().getConverter(variableClass).toBytes(output));

    controller.executeCopleyCommand(SET_VARIABLE, outputBytes);
  }

  public void copyToBank(MotorAxis axis) {
    VariableIdentifier variableID = getVariableID(id, axis, bank);

    byte[] outputBytes = controller.getConverters().getConverter(VariableIdentifier.class).toBytes(
        variableID);

    controller.executeCopleyCommand(COPY_VARIABLE, outputBytes);
  }

  @Override
  public VariableBank getBank() {
    return bank;
  }

  @Override
  public Optional<? extends Variable<U>> trySwitchBank(VariableBank bank) {
    return Optional.empty();
  }
}
