package uk.co.saiman.comms.copley;

import java.util.stream.Stream;

public interface CopleyNode {
  int getIndex();

  OperatingMode getOperatingMode();

  void setOperatingMode(OperatingMode mode);

  Stream<CopleyAxis> getAxes();
}
