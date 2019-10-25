package uk.co.saiman.eclipse.ui.fx.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.eclipse.ui.TransferFormat;

public class TransferIn {
  private final Map<TransferFormat<?>, Supplier<?>> values;

  public TransferIn() {
    this.values = Map.of();
  }

  private TransferIn(Map<TransferFormat<?>, Supplier<?>> values) {
    this.values = values;
  }

  public <T> TransferIn with(TransferFormat<T> format, Supplier<T> value) {
    var values = new HashMap<>(this.values);
    values.put(format, value);
    return new TransferIn(values);
  }

  public <T> T getValue(TransferFormat<T> format) {
    @SuppressWarnings("unchecked")
    Supplier<T> value = (Supplier<T>) values.get(format);
    if (value == null) {
      throw new IllegalArgumentException();
    }
    return value.get();
  }

  public Stream<TransferFormat<?>> getTransferFormats() {
    return values.keySet().stream();
  }
}
