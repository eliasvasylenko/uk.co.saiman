package uk.co.saiman.eclipse.ui.fx.impl;

import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.eclipse.ui.TransferFormat;
import uk.co.saiman.eclipse.ui.TransferMode;

class TransferFormatImpl<T> implements TransferFormat<T> {
  private final Set<TransferMode> transferModes;
  private final DataFormat<T> format;

  public TransferFormatImpl(
      Collection<? extends TransferMode> transferModes,
      DataFormat<T> format) {
    this.transferModes = unmodifiableSet(new HashSet<>(transferModes));
    this.format = format;
  }

  @Override
  public Set<TransferMode> transferModes() {
    return transferModes;
  }

  @Override
  public DataFormat<T> dataFormat() {
    return format;
  }
}