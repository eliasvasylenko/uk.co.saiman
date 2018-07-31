package uk.co.saiman.eclipse.treeview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import uk.co.saiman.eclipse.ui.TransferIn;
import uk.co.saiman.eclipse.ui.TransferMode;

public class DropHandler<T> {
  private final List<TransferMode> transferModes;
  private final Predicate<? super TransferIn<? extends T>> checkDrop;
  private final Consumer<? super TransferIn<? extends T>> handleDrop;

  public DropHandler(
      Collection<? extends TransferMode> transferModes,
      Predicate<? super TransferIn<? extends T>> checkDrop,
      Consumer<? super TransferIn<? extends T>> handleDrop) {
    this.transferModes = new ArrayList<>(transferModes);
    this.checkDrop = checkDrop;
    this.handleDrop = handleDrop;
  }

  public Stream<TransferMode> getTransferModes() {
    return transferModes.stream();
  }

  public boolean checkDrop(TransferIn<? extends T> candidate) {
    return checkDrop.test(candidate);
  }

  public void handleDrop(TransferIn<? extends T> candidate) {
    handleDrop.accept(candidate);
  }
}