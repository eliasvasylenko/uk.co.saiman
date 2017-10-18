package uk.co.saiman.msapex.saint;

import java.util.Collection;
import java.util.stream.Stream;

import javax.measure.Quantity;

import uk.co.saiman.msapex.instrument.stage.StageDiagramSampleConfiguration;
import uk.co.saiman.msapex.instrument.stage.XYStageDiagram;

public class SaintStageDiagram extends XYStageDiagram {
  

  @Override
  public Stream<Quantity<?>> getCoordinatesAtPixel(int pixelX, int pixelY) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getPixelXAtCoordinates(Collection<? extends Quantity<?>> coordinates) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getPixelYAtCoordinates(Collection<? extends Quantity<?>> coordinates) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Stream<? extends StageDiagramSampleConfiguration> getSampleConfigurations() {
    // TODO Auto-generated method stub
    return null;
  }
}
