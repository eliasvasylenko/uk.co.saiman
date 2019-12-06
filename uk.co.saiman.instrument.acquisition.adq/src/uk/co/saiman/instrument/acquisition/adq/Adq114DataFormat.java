package uk.co.saiman.instrument.acquisition.adq;

public enum Adq114DataFormat {
  PACKED_14BIT, UNPACKED_14BIT, UNPACKED_16BIT, UNPACKED_32BIT;

  /*
   * There are many more trigger modes documented, but we probably won't need
   * them. If you need others, check the ADQAPI docs.
   */

  public int toInt() {
    return ordinal();
  }

  public static Adq114DataFormat fromInt(int value) {
    return values()[value];
  }
}
