package uk.co.saiman.instrument.acquisition.adq;

public enum TestPatternMode {
  NORMAL_OPERATION,
  CONSTANT,
  COUNT_UP,
  COUNT_DOWN,
  COUNT_ALTERNATING,
  RESERVED_1,
  RESERVED_2,
  MERGE_GPIO;

  public int toInt() {
    return ordinal();
  }

  public static TestPatternMode fromInt(int value) {
    return values()[value];
  }
}
