package uk.co.saiman.eclipse.model.ui.provider.editor;

import static uk.co.saiman.eclipse.model.ui.Package.eINSTANCE;

import org.eclipse.jface.resource.ImageDescriptor;

import uk.co.saiman.eclipse.model.ui.Package;
import uk.co.saiman.eclipse.model.ui.provider.editor.ListComponentManager.Type;

public class VCellContributionsEditor extends VListEditor {
  public VCellContributionsEditor() {
    super(
        Package.eINSTANCE.getCell_Contributions(),
        new Type(
            getString("_UI_CellContribution_type"),
            ImageDescriptor
                .createFromFile(VListEditor.class, "/icons/full/obj16/CellContribution.gif"),
            eINSTANCE.getCellContribution()));
  }
}
