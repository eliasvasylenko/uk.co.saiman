/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.util.function.Supplier;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.e4.ui.internal.workbench.E4XMIResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import uk.co.saiman.eclipse.model.ui.Cell;

public class CellToStringConverter extends Converter {
  private final Supplier<String> none;

  public CellToStringConverter(Class<?> modelClass, Supplier<String> none) {
    super(modelClass, String.class);
    this.none = none;
  }

  @Override
  public String convert(Object fromObject) {
    final Cell cmd = (Cell) fromObject;
    String elementId = null;
    if (cmd != null && cmd.getElementId() != null && cmd.getElementId().trim().length() > 0) {
      elementId = cmd.getElementId();
    }
    if (cmd == null) {
      return none.get();
    } else if (cmd.getLabel() != null && cmd.getLabel().trim().length() > 0) {
      return cmd.getLabel() + (elementId != null ? " - " + elementId : ""); //$NON-NLS-1$//$NON-NLS-2$
    } else if (elementId != null) {
      return elementId;
    } else {
      final Resource res = ((EObject) cmd).eResource();
      if (res instanceof E4XMIResource) {
        final String v = ((E4XMIResource) res).getID((EObject) cmd);
        if (v != null && v.trim().length() > 0) {
          return v;
        }
      }
      return cmd.getClass().getSimpleName() + "@" + cmd.hashCode();
    }
  }
}
