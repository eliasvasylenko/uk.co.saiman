package uk.co.saiman.comms.copley;

public interface CopleyAxis {
  int getAxisNumber();

  /*
   * TODO once enums have generic types the variable IDs can be typed and the
   * signature of this method should be modified to reflect that generically.
   */
  Variable<?> variable(CopleyVariableID id);

  Variable<Int32> actualPosition();

  WritableVariable<Int32> requestedPosition();
}
