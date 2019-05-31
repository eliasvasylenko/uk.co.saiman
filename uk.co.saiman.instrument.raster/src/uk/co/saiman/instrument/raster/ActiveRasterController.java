package uk.co.saiman.instrument.raster;

public interface ActiveRasterController extends RasterController {
  void setPattern(RasterPattern pattern);

  void setWidth(int width);

  void setHeight(int height);
}
