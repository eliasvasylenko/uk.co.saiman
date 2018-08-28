package uk.co.saiman.eclipse.model.ui.provider.editor;

import static uk.co.saiman.eclipse.model.ui.Package.eINSTANCE;

import org.eclipse.e4.tools.emf.ui.common.IEditorDescriptor;
import org.eclipse.emf.ecore.EClass;

public class HandledCellEditorDescriptor implements IEditorDescriptor {
  @Override
  public EClass getEClass() {
    return eINSTANCE.getHandledCell();
  }

  @Override
  public Class<?> getEditorClass() {
    return HandledCellEditor.class;
  }
}
