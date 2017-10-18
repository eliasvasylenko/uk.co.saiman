package uk.co.saiman.comms.copley.simulation;

import uk.co.saiman.comms.ByteConverter;
import uk.co.saiman.comms.copley.VariableBank;

abstract class ComputedVariable<T> implements SimulatedVariable {
  private final ByteConverter<T> converter;

  public ComputedVariable(ByteConverter<T> converter) {
    this.converter = converter;
  }

  @Override
  public byte[] get(int axis, VariableBank bank) {
    switch (bank) {
    case ACTIVE:
      return converter.toBytes(compute(axis));
    default:
      throw new UnsupportedOperationException();
    }
  }

  public abstract T compute(int axis);

  @Override
  public void set(int axis, VariableBank bank, byte[] value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void copy(byte axis, VariableBank bank) {
    throw new UnsupportedOperationException();
  }
}