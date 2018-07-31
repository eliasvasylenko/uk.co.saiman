package uk.co.saiman.eclipse.treeview.impl;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.eclipse.treeview.DragHandler;
import uk.co.saiman.eclipse.treeview.DropHandler;
import uk.co.saiman.eclipse.ui.FormatConverter;

public interface ItemGroup<T> {
  boolean isSettable();

  Optional<String> contributionId();

  Optional<Object> anonymousContribution();

  Stream<FormatConverter<T>> formatConverters();

  Stream<DragHandler<T>> dragHandlers();

  Stream<DropHandler<T>> dropHandlers();
}
