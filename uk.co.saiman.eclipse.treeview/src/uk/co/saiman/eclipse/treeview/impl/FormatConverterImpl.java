package uk.co.saiman.eclipse.treeview.impl;

import java.util.function.Function;

import javafx.scene.input.DataFormat;
import uk.co.saiman.eclipse.ui.FormatConverter;

class FormatConverterImpl<T> implements FormatConverter<T> {
  private final DataFormat format;
  private final Function<? super T, String> encode;
  private final Function<String, ? extends T> decode;

  public FormatConverterImpl(
      DataFormat format,
      Function<? super T, String> encode,
      Function<String, ? extends T> decode) {
    this.format = format;
    this.encode = encode;
    this.decode = decode;
  }

  @Override
  public DataFormat format() {
    return format;
  }

  @Override
  public String encode(T object) {
    return encode.apply(object);
  }

  @Override
  public T decode(String data) {
    return decode.apply(data);
  }
}