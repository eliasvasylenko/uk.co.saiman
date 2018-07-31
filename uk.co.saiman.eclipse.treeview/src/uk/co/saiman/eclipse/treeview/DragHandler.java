package uk.co.saiman.eclipse.treeview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import uk.co.saiman.eclipse.ui.TransferMode;
import uk.co.saiman.eclipse.ui.TransferOut;

public class DragHandler<T> {
  private final List<TransferMode> transferModes;
  private final Predicate<? super TransferOut<? extends T>> checkDrag;
  private final Consumer<? super TransferOut<? extends T>> handleDrag;

  public DragHandler(
      Collection<? extends TransferMode> transferModes,
      Predicate<? super TransferOut<? extends T>> checkDrag,
      Consumer<? super TransferOut<? extends T>> handleDrag) {
    this.transferModes = new ArrayList<>(transferModes);
    this.checkDrag = checkDrag;
    this.handleDrag = handleDrag;
  }

  public Stream<TransferMode> getTransferModes() {
    return transferModes.stream();
  }

  public boolean checkDrag(TransferOut<? extends T> candidate) {
    return checkDrag.test(candidate);
  }

  public void handleDrag(TransferOut<? extends T> candidate) {
    handleDrag.accept(candidate);
  }
}
