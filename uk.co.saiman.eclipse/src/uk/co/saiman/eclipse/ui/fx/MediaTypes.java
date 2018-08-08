package uk.co.saiman.eclipse.ui.fx;

import java.util.stream.Stream;

import javafx.scene.input.DataFormat;
import uk.co.saiman.data.format.MediaType;

public class MediaTypes {
  private MediaTypes() {}

  public static DataFormat toDataFormat(MediaType mediaType) {
    DataFormat format = DataFormat.lookupMimeType(mediaType.toString());
    if (format == null) {
      format = new DataFormat(mediaType.toString());
    }
    return format;
  }

  public static Stream<MediaType> fromDataFormat(DataFormat dataFormat) {
    return dataFormat.getIdentifiers().stream().map(MediaType::parse);
  }
}
