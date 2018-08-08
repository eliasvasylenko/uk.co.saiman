package uk.co.saiman.eclipse.ui.fx;

import java.util.Optional;

import uk.co.saiman.eclipse.ui.TransferMode;

public class TransferModes {
  private TransferModes() {}

  public static Optional<javafx.scene.input.TransferMode> toJavaFXTransferMode(TransferMode from) {
    switch (from) {
    case COPY:
      return Optional.of(javafx.scene.input.TransferMode.COPY);
    case LINK:
      return Optional.of(javafx.scene.input.TransferMode.LINK);
    case MOVE:
      return Optional.of(javafx.scene.input.TransferMode.MOVE);
    case DISCARD:
      return Optional.empty();
    default:
      throw new AssertionError();
    }
  }

  public static TransferMode fromJavaFXTransferMode(javafx.scene.input.TransferMode from) {
    switch (from) {
    case COPY:
      return TransferMode.COPY;
    case LINK:
      return TransferMode.LINK;
    case MOVE:
      return TransferMode.MOVE;
    default:
      throw new AssertionError();
    }
  }
}
