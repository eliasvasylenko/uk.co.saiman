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
package uk.co.saiman.msapex.editor;

import java.util.stream.Stream;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

/**
 * An editor provider for certain types of resource. Implementations may manage
 * abstract resource descriptors or operate directly on business objects.
 * <p>
 * The provider can provide a number of different editors for different types of
 * resource. Each provided editor has to a unique {@link #getEditorPartIds ID},
 * where each ID corresponds to the {@link MPart#getElementId element id} of the
 * editor part which can be added to the application model.
 * 
 * @author Elias N Vasylenko
 */
public interface EditorProvider {
  String getId();

  /**
   * Get a list of the available editor part IDs.
   * 
   * @return a list of ID strings corresponding to the {@link MPart#getElementId
   *         element IDs} of the MPart instances we can instantiate
   */
  Stream<String> getEditorPartIds();

  /**
   * Test whether an editor is applicable to a resource.
   * 
   * @param editorId
   *          the ID of the editor
   * @param resource
   *          the object to edit
   * @return true if the editor with the given part ID is applicable to the given
   *         resource
   */
  boolean isEditorApplicable(String editorId, Object resource);

  /**
   * Create a {@link MPart part} instance of the given element ID.
   * <p>
   * This method is invoked the first time a part is to be added to the model, and
   * <em>not</em> when a persisted part is reloaded into the model.
   * 
   * @param id
   *          the ID of the editor part to create
   * @param resource
   *          the object to edit
   * @return a new editor part, not yet added to the application model
   */
  MPart createEditorPart(String id, Object resource);

  /**
   * Load the resource for a persisted editor.
   * 
   * @param part
   *          the editor part which has been persisted and loaded into the
   *          application model
   * @return a reference to the editing resource loaded from the persisted state
   */
  Object loadEditorResource(MPart part);

  /**
   * Initialize an editor part for the given resource.
   * <p>
   * This method will be invoked both when a
   * {@link #createEditorPart(String, Object) new part instance is created} and
   * when an {@link #loadEditorResource(MPart) existing part} is loaded from the
   * persisted application model.
   * <p>
   * The management system is guaranteed to only invoke this method for a part
   * which has been {@link #createEditorPart(String) created via an id} which has
   * been {@link #getCompatibleResource(String, Object) verified to be compatible}
   * with the given resource.
   * <p>
   * It is also guaranteed that this method will be invoked after the part's
   * {@link IEclipseContext context} has been initialized, but before the part's
   * {@link MContribution#getContributionURI() contribution} has been created.
   * 
   * @param part
   *          the editor part which is being entered into the application model
   * @param resource
   *          the object to edit
   */
  void initializeEditorPart(MPart part, Object resource);
}
