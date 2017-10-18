package uk.co.saiman.comms.copley.simulation;

import static uk.co.saiman.comms.copley.VariableBank.ACTIVE;
import static uk.co.saiman.comms.copley.VariableBank.STORED;
import static uk.co.saiman.comms.copley.impl.CopleyCommsImpl.WORD_SIZE;

import java.util.ArrayList;
import java.util.List;

import uk.co.saiman.comms.copley.VariableBank;

class ByteVariable implements SimulatedVariable {
  private final List<byte[]> active;
  private final List<byte[]> defaults;

  public ByteVariable(int axes, int words) {
    active = new ArrayList<>(axes);
    defaults = new ArrayList<>(axes);

    byte[] identity = new byte[words * WORD_SIZE];
    for (int i = 0; i < axes; i++) {
      active.add(identity);
      defaults.add(identity);
    }
  }

  @Override
  public byte[] get(int axis, VariableBank bank) {
    switch (bank) {
    case ACTIVE:
      return active.get(axis);
    case STORED:
      return defaults.get(axis);
    default:
      throw new AssertionError();
    }
  }

  @Override
  public void set(int axis, VariableBank bank, byte[] value) {
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
  public void copy(byte axis, VariableBank bank) {
    set(axis, bank, get(axis, bank == ACTIVE ? STORED : ACTIVE));
  }
}