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
