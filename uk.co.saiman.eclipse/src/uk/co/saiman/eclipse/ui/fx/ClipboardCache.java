package uk.co.saiman.eclipse.ui.fx;

import java.util.Optional;

import uk.co.saiman.data.format.DataFormat;

public interface ClipboardCache {
  <T> Optional<T> getData(DataFormat<T> format);
}
