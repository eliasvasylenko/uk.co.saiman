package uk.co.saiman.eclipse.ui.fx;

import javafx.scene.input.Dragboard;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.TransferDestination;

public interface TransferCellHandler {
  TransferCellOut transferOut(Cell cell);

  TransferCellIn transferIn(Cell cell, Dragboard clipboard, TransferDestination position);
}
