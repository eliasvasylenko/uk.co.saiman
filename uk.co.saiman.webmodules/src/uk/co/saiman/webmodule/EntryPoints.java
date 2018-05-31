package uk.co.saiman.webmodule;

import java.util.HashMap;
import java.util.Map;

public final class EntryPoints {
  private static final EntryPoints EMPTY = new EntryPoints();
  private final Map<ModuleFormat, EntryPoint> entryPoints;

  private EntryPoints() {
    entryPoints = new HashMap<>();
  }

  private EntryPoints(Map<ModuleFormat, EntryPoint> entryPoints) {
    this.entryPoints = new HashMap<>(entryPoints);
  }

  public static EntryPoints empty() {
    return EMPTY;
  }

  public EntryPoints withEntryPoint(EntryPoint path) {
    Map<ModuleFormat, EntryPoint> entryPoints = new HashMap<>(this.entryPoints);
    entryPoints.put(path.format(), path);
    return new EntryPoints(entryPoints);
  }

  public EntryPoint getEntryPoint(ModuleFormat format) {
    return entryPoints.get(format);
  }
}
