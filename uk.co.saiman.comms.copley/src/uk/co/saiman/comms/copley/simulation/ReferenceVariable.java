package uk.co.saiman.comms.copley.simulation;

import static uk.co.saiman.comms.copley.VariableBank.ACTIVE;
import static uk.co.saiman.comms.copley.VariableBank.STORED;

import java.util.ArrayList;
import java.util.List;

import uk.co.saiman.comms.ByteConverter;
import uk.co.saiman.comms.copley.VariableBank;

class ReferenceVariable<T> implements SimulatedVariable {
  private final ByteConverter<T> converter;
  private final List<T> active;
  private final List<T> defaults;

  public ReferenceVariable(int axes, ByteConverter<T> converter) {
    active = new ArrayList<>(axes);
    defaults = new ArrayList<>(axes);
    this.converter = converter;

    T identity = converter.create();
    for (int i = 0; i < axes; i++) {
      active.add(identity);
      defaults.add(identity);
    }
  }

  ByteConverter<T> getConverter() {
    return converter;
  }

  public T getReference(int axis, VariableBank bank) {
    switch (bank) {
    case ACTIVE:
      return active.get(axis);
    case STORED:
      return defaults.get(axis);
    default:
      throw new AssertionError();
    }
  }

  public void setReference(int axis, VariableBank bank, T value) {
    switch (bank) {
    case ACTIVE:
      active.set(axis, value);
      break;
    case STORED:
      defaults.set(axis, value);
      break;
    default:
      throw new AssertionError();
    }
  }

  @Override
  public byte[] get(int axis, VariableBank bank) {
    return converter.toBytes(getReference(axis, bank));
  }

  @Override
  public void set(int axis, VariableBank bank, byte[] value) {
    setReference(axis, bank, converter.fromBytes(value));
  }

  @Override
  public void copy(byte axis, VariableBank bank) {
    set(axis, bank, get(axis, bank == ACTIVE ? STORED : ACTIVE));
  }
}
