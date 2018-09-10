package uk.co.saiman.eclipse.ui.fx.impl;

import static uk.co.saiman.eclipse.ui.TransferMode.COPY;
import static uk.co.saiman.eclipse.ui.TransferMode.LINK;

import java.util.EnumSet;
import java.util.Set;

import javax.inject.Inject;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.TransferDestination;
import uk.co.saiman.eclipse.ui.TransferMode;
import uk.co.saiman.eclipse.ui.fx.TransferCellHandler;
import uk.co.saiman.eclipse.ui.fx.TransferCellIn;
import uk.co.saiman.eclipse.ui.fx.TransferCellOut;

public class DefaultTransferCellHandler implements TransferCellHandler {
  @Inject
  public DefaultTransferCellHandler() {}

  @Override
  public TransferCellOut transferOut(Cell cell) {
    boolean modifiable = isModifiable(cell) && cell.isOptional();

    ClipboardContent content = serialize(cell);

    if (content.isEmpty()) {
      return TransferCellOut.UNSUPPORTED;
    }

    return new TransferCellOut() {
      @Override
      public Set<TransferMode> supportedTransferModes() {
        return modifiable ? EnumSet.allOf(TransferMode.class) : EnumSet.of(COPY, LINK);
      }

      @Override
      public void handle(TransferMode transferMode) {
        System.out.println("handle transfer out " + transferMode + " ? " + modifiable);
        if (modifiable && transferMode.isDestructive()) {
          cell.getContext().modify(cell.getContextValue(), null);
        }
      }

      @Override
      public ClipboardContent getClipboardContent() {
        return content;
      }
    };
  }

  @Override
  public TransferCellIn transferIn(Cell cell, Dragboard clipboard, TransferDestination position) {
    if (position != TransferDestination.OVER) {
      return TransferCellIn.UNSUPPORTED;
    }

    boolean modifiable = isModifiable(cell);

    Object value = deserialize(cell, clipboard);

    return new TransferCellIn() {
      @Override
      public Set<TransferMode> supportedTransferModes() {
        return EnumSet.allOf(TransferMode.class);
      }

      @Override
      public void handle(TransferMode transferMode) {
        System.out.println("handle transfer in " + transferMode + " ? " + modifiable);
        if (transferMode.isDestructive()) {
          cell.getContext().modify(cell.getContextValue(), null);
        }
      }
    };
  }

  static ClipboardContent serialize(Cell cell) {
    cell.getTransferFormats();
    return new ClipboardContent(); // TODO
  }

  static Object deserialize(Cell cell, Clipboard content) {
    cell.getTransferFormats();
    return null; // TODO
  }

  static boolean isModifiable(Cell cell) {
    if (!cell.getContext().containsKey(cell.getContextValue())) {
      return false;
    }

    try {
      cell
          .getContext()
          .modify(cell.getContextValue(), cell.getContext().get(cell.getContextValue()));
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
