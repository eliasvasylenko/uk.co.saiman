package uk.co.saiman.eclipse.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TransferSource {
  private final Map<TransferFormat<?>, Object> values;

  public TransferSource() {
    this.values = Map.of();
  }

  private TransferSource(Map<TransferFormat<?>, Object> values) {
    this.values = values;
  }

  public <T> TransferSource with(TransferFormat<T> format, T value) {
    var values = new HashMap<>(this.values);
    values.put(format, value);
    return new TransferSource(values);
  }

  @SuppressWarnings("unchecked")
  public <T> T getValue(TransferFormat<T> format) {
    if (!values.containsKey(format)) {
      throw new IllegalArgumentException();
    }
    return (T) values.get(format);
  }

  public Stream<TransferFormat<?>> getTransferFormats() {
    return values.keySet().stream();
  }
}
