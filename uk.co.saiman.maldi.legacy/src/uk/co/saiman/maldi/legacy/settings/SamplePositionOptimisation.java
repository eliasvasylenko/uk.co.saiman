package uk.co.saiman.maldi.legacy.settings;

import uk.co.saiman.instrument.raster.RasterPattern;

public class SamplePositionOptimisation {
  public enum Guidance {
    CONSERVATIVE, LIBERAL, MANUAL
  }

  private final Guidance guidance;
  private final RasterPattern rasterPattern;
  private final int window;

  public SamplePositionOptimisation(Guidance guidance, RasterPattern rasterPattern, int window) {
    this.guidance = guidance;
    this.rasterPattern = rasterPattern;
    this.window = window;
  }

  Guidance guidance() {
    return guidance;
  }

  RasterPattern rasterPattern() {
    return rasterPattern;
  }

  int window() {
    return window;
  }
}
