package uk.co.saiman.msapex.editor.impl;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import uk.co.saiman.msapex.editor.Editor;

public class PartEditor implements Editor {
  private final MPart editor;

  public PartEditor(MPart editor) {
    this.editor = editor;
  }

  @Override
  public String getLabel() {
    return editor.getLocalizedLabel();
  }

  @Override
  public String getDescription() {
    return editor.getLocalizedDescription();
  }

  @Override
  public String getIconURI() {
    return editor.getIconURI();
  }

  @Override
  public MPart openPart() {
    return editor;
  }
}
