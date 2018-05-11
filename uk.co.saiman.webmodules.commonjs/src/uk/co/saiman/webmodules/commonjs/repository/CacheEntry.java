package uk.co.saiman.webmodules.commonjs.repository;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newOutputStream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import uk.co.saiman.webmodules.commonjs.registry.RegistryResolutionException;

public class CacheEntry {
  private final Path location;

  public CacheEntry(Path location) {
    this.location = location;
  }

  public Path getLocation() {
    return location;
  }

  public Path writeBytes(byte[] bytes) {
    return writeBytesImpl(getLocation(), bytes);
  }

  public Path writeBytes(String resourceName, byte[] bytes) {
    return writeBytesImpl(getLocation().resolve(resourceName), bytes);
  }

  public Path writeBytes(Path destination, byte[] bytes) {
    return writeBytesImpl(getLocation().resolve(destination), bytes);
  }

  private Path writeBytesImpl(Path destination, byte[] bytes) {
    try {
      createDirectories(destination.getParent());

      try (BufferedOutputStream buffered = new BufferedOutputStream(newOutputStream(destination))) {
        buffered.write(bytes);
      }

      return destination;
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to write bytes to cache directory " + destination,
          e);
    }
  }

  /**
   * In case two instances are trying to write to the same location, i.e. one or
   * more registry instances handling multiple resource with the same sha1, we
   * must guard by writing to a unique, safe location then moving to the cache
   * location when done. If we find something is there by the time we're finished
   * we can just discard our work and use the existing entry.
   */
  void complete() {
    // TODO
  }
}
