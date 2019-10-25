package uk.co.saiman.eclipse.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TransferSink {
  private final Map<TransferFormat<?>, Consumer<?>> values;

  public TransferSink() {
    this.values = Map.of();
  }

  private TransferSink(Map<TransferFormat<?>, Consumer<?>> values) {
    this.values = values;
  }

  public <T> TransferSink with(TransferFormat<T> format, Consumer<T> value) {
    var values = new HashMap<>(this.values);
    values.put(format, value);
    return new TransferSink(values);
  }

  @SuppressWarnings("unchecked")
  public <T> void putValue(TransferFormat<T> format, T value) {
    if (!values.containsKey(format)) {
      throw new IllegalArgumentException();
    }
    ((Consumer<T>) values.get(format)).accept(value);
  }

  public Stream<TransferFormat<?>> getTransferFormats() {
    return values.keySet().stream();
  }
}
