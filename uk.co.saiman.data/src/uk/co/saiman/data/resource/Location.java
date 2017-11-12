package uk.co.saiman.data.resource;

import java.util.stream.Stream;

public interface Location {
  Stream<Resource> getResources();

  Resource getResource(String name, String extension);
}
