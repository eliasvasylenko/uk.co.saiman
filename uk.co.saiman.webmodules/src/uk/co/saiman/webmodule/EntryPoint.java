package uk.co.saiman.webmodule;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class EntryPoint {
  private final ModuleFormat format;
  private final Path path;

  public EntryPoint(ModuleFormat format, Path path) {
    this.format = format;
    this.path = path;
  }

  public EntryPoint(ModuleFormat format, String path) {
    this(format, Paths.get(path));
  }

  public ModuleFormat format() {
    return format;
  }

  public Path path() {
    return path;
  }
}
