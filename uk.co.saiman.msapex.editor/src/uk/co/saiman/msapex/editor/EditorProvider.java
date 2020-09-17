/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

public interface EditorProvider {
  public static final String DEFAULT_EDITOR_CONTEXT_KEY = "DefaultEditorContextKey";
  public static final String DEFAULT_EDITOR_PROVIDER = "DefaultEditorProvider";

  String getContextKey();

  /**
   * Determine whether a given context value can be {@link #getEditorPart(Object)
   * opened} by this editor provider. If this returns true, implementors should
   * try to guarantee that the value will be compatible, though this may not
   * always be possible. In some cases this method may only give a heuristic, e.g.
   * determining compatibility by checking a file extension, in which case it may
   * be possible to open an editor for a value for which this method returns
   * false.
   * 
   * @param contextValue
   * @return true if the value is expected to be compatible, false otherwise
   */
  boolean isApplicable(Object contextValue);

  /**
   * Create an editor part which is applicable to the given context key and value,
   * or get an existing part if one is already created for the given value.
   * <p>
   * The returned part should not be activated, and if it is newly created as a
   * result of invocation, it should not be added to the application model.
   * <p>
   * If the given value was previously signaled to be {@link #isApplicable(Object)
   * applicable} then implementations should try to guarantee that invocation will
   * succeed and return a valid editor instance. They may also give a best-effort
   * in the case that {@link #isApplicable(Object)} returned false.
   * 
   * @param contextKey
   * @param contextValue
   * @return an optional containing an editor part which is applicable for the
   *         given context value, or an empty optional if no such part can be
   *         created or found
   */
  Editor getEditorPart(Object contextValue);
}
