/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.eclipse.ui.edit.
 *
 * uk.co.saiman.eclipse.ui.edit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.ui.edit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
