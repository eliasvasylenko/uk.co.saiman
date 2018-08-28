package uk.co.saiman.eclipse.model.ui.provider.editor;

import static uk.co.saiman.eclipse.model.ui.Package.eINSTANCE;

import org.eclipse.e4.tools.emf.ui.common.IModelResource;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.swt.widgets.Shell;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.CellContribution;

public class CellContributionCellSelectionDialog extends AbstractCellSelectionDialog {
  private final CellContribution cellContribution;

  public CellContributionCellSelectionDialog(
      Shell parentShell,
      CellContribution cellContribution,
      IModelResource resource,
      ResourceLocator resourceLocator) {
    super(parentShell, resource, resourceLocator);
    this.cellContribution = cellContribution;
  }

  @Override
  protected String getShellTitle() {
    return "%%% shell title";
  }

  @Override
  protected String getDialogTitle() {
    return "%%% dialog title";
  }

  @Override
  protected String getDialogMessage() {
    return "%%% dialog message";
  }

  @Override
  protected Command createStoreCommand(EditingDomain editingDomain, Cell cell) {
    return SetCommand
        .create(editingDomain, cellContribution, eINSTANCE.getCellContribution_Parent(), cell);
  }
}
