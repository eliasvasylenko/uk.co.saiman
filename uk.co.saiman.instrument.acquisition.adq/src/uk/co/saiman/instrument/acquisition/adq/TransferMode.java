package uk.co.saiman.instrument.acquisition.adq;

public enum TransferMode {
  NORMAL, HEADER_ONLY;

  public int toInt() {
    return ordinal();
  }

  public static TransferMode fromInt(int value) {
    return values()[value];
  }
}
