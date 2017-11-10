/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.msapex.editor.impl;

import static org.eclipse.e4.ui.services.IServiceConstants.ACTIVE_SELECTION;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import uk.co.saiman.eclipse.AdaptNamed;
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.msapex.editor.EditorProperties;
import uk.co.saiman.msapex.editor.EditorPrototype;
import uk.co.saiman.msapex.editor.EditorService;

public class OpenSelectionHandler {
  @CanExecute
  boolean canExecute(
      EditorService editorService,
      @Localize EditorProperties text,
      @Optional @AdaptNamed(ACTIVE_SELECTION) Object selection) {
    return selection != null && editorService.getApplicableEditors(selection).findAny().isPresent();
  }

  @Execute
  void execute(
      EditorService editorService,
      @Localize EditorProperties text,
      @AdaptNamed(ACTIVE_SELECTION) Object selection) {
    editorService.getApplicableEditors(selection).findFirst().ifPresent(EditorPrototype::showPart);
  }
}
