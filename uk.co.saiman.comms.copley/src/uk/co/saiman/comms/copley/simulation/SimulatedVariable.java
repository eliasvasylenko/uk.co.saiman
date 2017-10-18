package uk.co.saiman.comms.copley.simulation;

import uk.co.saiman.comms.copley.VariableBank;

interface SimulatedVariable {
  public byte[] get(int axis, VariableBank bank);

  public void set(int axis, VariableBank bank, byte[] value);

  public void copy(byte axis, VariableBank bank);
}