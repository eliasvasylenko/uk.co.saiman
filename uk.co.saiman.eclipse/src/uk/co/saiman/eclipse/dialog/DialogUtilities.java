package uk.co.saiman.eclipse.dialog;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public final class DialogUtilities {
  private DialogUtilities() {}

  public static void addStackTrace(Alert alert, Throwable throwable) {
    // Create expandable Exception.
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    throwable.printStackTrace(pw);
    String exceptionText = sw.toString();

    addTextArea(alert, exceptionText);
  }

  public static void addTextArea(Alert alert, String text) {
    TextArea textArea = new TextArea(text);
    textArea.setEditable(false);
    textArea.setWrapText(true);

    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setMaxHeight(Double.MAX_VALUE);
    GridPane.setVgrow(textArea, Priority.ALWAYS);
    GridPane.setHgrow(textArea, Priority.ALWAYS);

    // Set expandable Exception into the dialog pane.
    alert.getDialogPane().setExpandableContent(textArea);
  }
}
