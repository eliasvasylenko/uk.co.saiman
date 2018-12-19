package uk.co.saiman.experiment.storage;

import java.io.IOException;

import uk.co.saiman.data.resource.Location;

public interface Storage {
  void deallocate() throws IOException;

  Location location();
}