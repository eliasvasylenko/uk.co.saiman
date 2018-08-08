package uk.co.saiman.eclipse.ui.fx;

import javafx.scene.input.Clipboard;

public interface ClipboardService {
  public ClipboardCache getCache(Clipboard clipboard);
}
