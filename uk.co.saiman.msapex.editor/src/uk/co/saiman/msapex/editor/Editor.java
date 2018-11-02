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
 * This file is part of uk.co.saiman.msapex.editor.
 *
 * uk.co.saiman.msapex.editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.editor;

import static java.util.Arrays.asList;

import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.MSnippetContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import uk.co.saiman.msapex.editor.impl.PartEditor;

public interface Editor {
  String getLabel();

  String getDescription();

  String getIconURI();

  MPart getPart();

  static Editor overPart(MPart part) {
    return new PartEditor(part);
  }

  static Editor overSnippet(MPart part, MSnippetContainer container) {
    return overPart(cloneSnippet(part, container));
  }

  static Editor overSnippet(String id, EModelService modelService, MSnippetContainer container) {
    return overPart(cloneSnippet(id, modelService, container));
  }

  static MPart cloneSnippet(MPart snippet, MSnippetContainer container) {
    return cloneSnippet(
        snippet.getElementId(),
        snippet.getContext().get(EModelService.class),
        container);
  }

  static MPart cloneSnippet(String id, EModelService modelService, MSnippetContainer container) {
    MPart part = (MPart) modelService.cloneSnippet(container, id, null);
    modelService
        .findElements(part, null, MApplicationElement.class, asList("renameOnClone"))
        .stream()
        .forEach(e -> e.setElementId(e.getElementId() + ".clone"));
    return part;
  }
}
