package uk.co.saiman.eclipse.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ContextBuffer {
  private final Map<String, Object> values = new HashMap<>();

  public static ContextBuffer empty() {
    return new ContextBuffer();
  }

  public ContextBuffer set(String name, Object value) {
    values.put(name, value);
    return this;
  }

  public <T> ContextBuffer set(Class<T> clazz, T value) {
    return set(clazz.getName(), value);
  }

  public Stream<String> keys() {
    return values.keySet().stream();
  }

  public Object get(String name) {
    return values.get(name);
  }

  public <T> T get(Class<T> clazz) {
    return clazz.cast(values.get(clazz.getName()));
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + values.toString();
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }
}
