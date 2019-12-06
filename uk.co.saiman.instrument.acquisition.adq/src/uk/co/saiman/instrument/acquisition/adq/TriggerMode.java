package uk.co.saiman.instrument.acquisition.adq;

public enum TriggerMode {
  SOFTWARE_TRIGGER, EXTERNAL_TRIGGER_1, LEVEL_TRIGGER, INTERNAL_TRIGGER;

  /*
   * There are many more trigger modes documented, but we probably won't need
   * them. If you need others, check the ADQAPI docs.
   */

  public int toInt() {
    return ordinal() + 1;
  }

  public static TriggerMode fromInt(int value) {
    return values()[value - 1];
  }
}
