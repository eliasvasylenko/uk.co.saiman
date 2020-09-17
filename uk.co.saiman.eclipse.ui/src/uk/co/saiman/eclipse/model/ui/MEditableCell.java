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
 * This file is part of uk.co.saiman.eclipse.ui.
 *
 * uk.co.saiman.eclipse.ui is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.ui is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 */
package uk.co.saiman.eclipse.model.ui;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Editable Cell</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.MEditableCell#isEditing <em>Editing</em>}</li>
 * </ul>
 *
 * @see uk.co.saiman.eclipse.model.ui.MPackage#getEditableCell()
 * @model
 * @generated
 */
public interface MEditableCell extends MCell {
  /**
   * Returns the value of the '<em><b>Editing</b></em>' attribute.
   * The default value is <code>"false"</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Whether the cell is currently in the editing mode. During editing, if the current edit state represents a valid input, the result of applying the edit should be placed into the transient data with the key of the cells context value.
   * <!-- end-model-doc -->
   * @return the value of the '<em>Editing</em>' attribute.
   * @see #setEditing(boolean)
   * @see uk.co.saiman.eclipse.model.ui.MPackage#getEditableCell_Editing()
   * @model default="false"
   * @generated
   */
  boolean isEditing();

  /**
   * Sets the value of the '{@link uk.co.saiman.eclipse.model.ui.MEditableCell#isEditing <em>Editing</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Editing</em>' attribute.
   * @see #isEditing()
   * @generated
   */
  void setEditing(boolean value);

} // MEditableCell
