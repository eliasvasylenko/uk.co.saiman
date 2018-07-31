package uk.co.saiman.eclipse.ui;

import javafx.scene.input.DataFormat;

public interface FormatConverter<T> {
  DataFormat format();

  String encode(T object);

  T decode(String data);
}
