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

import java.util.stream.Stream;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public interface EditorService {
  void registerProvider(EditorProvider provider);

  void unregisterProvider(EditorProvider provider);

  /**
   * Get the available editors for a resource in order of precedence.
   * <p>
   * The strategy for determining the precedence is left to the implementer, but
   * generally it may be in order of the editors most recently
   * {@link EditorPrototype#openEditor() opened}.
   * 
   * @param resource
   *          the resource data object to edit
   * @return The current editors applicable to the given resource in order of
   *         precedence.
   */
  Stream<EditorPrototype> getApplicableEditors(Object resource);

  Stream<EditorDescriptor> getEditors();

  boolean isEditor(MPart part);

  Object getResource(MPart part);
}
