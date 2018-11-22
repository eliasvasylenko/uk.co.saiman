package uk.co.saiman.experiment;

import java.io.IOException;

import uk.co.saiman.data.resource.Location;

public interface Storage {
  void dispose() throws IOException;

  Location location();
}