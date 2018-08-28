package uk.co.saiman.eclipse.model.ui.provider.editor;

import static uk.co.saiman.eclipse.model.ui.Package.eINSTANCE;

import org.eclipse.e4.tools.emf.ui.common.IEditorDescriptor;
import org.eclipse.emf.ecore.EClass;

public class CellEditorDescriptor implements IEditorDescriptor {
  @Override
  public EClass getEClass() {
    return eINSTANCE.getCell();
  }

  @Override
  public Class<?> getEditorClass() {
    return CellEditor.class;
  }
}
