package uk.co.saiman.data.format;

import java.util.stream.Stream;

public interface DataFormatService {
  DataFormat<?> getDataFormat(MediaType mediaType);

  Stream<DataFormat<?>> getDataFormats(MediaType mediaType);
}
