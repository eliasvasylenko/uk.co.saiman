package uk.co.saiman.msapex.editor;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public interface Editor {
  String getLabel();

  String getDescription();

  String getIconURI();

  MPart openPart();
}
