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

import org.eclipse.e4.ui.model.application.MContribution;

import org.eclipse.e4.ui.model.application.commands.MHandlerContainer;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUILabel;

import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Cell</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.Cell#getPopupMenu <em>Popup Menu</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.Cell#isExpanded <em>Expanded</em>}</li>
 * </ul>
 *
 * @see uk.co.saiman.eclipse.model.ui.Package#getCell()
 * @model
 * @generated
 */
public interface Cell extends MUILabel, MContext, MContribution, MElementContainer<Cell>, MHandlerContainer {
  /**
   * Returns the value of the '<em><b>Popup Menu</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Popup Menu</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Popup Menu</em>' containment reference.
   * @see #setPopupMenu(MPopupMenu)
   * @see uk.co.saiman.eclipse.model.ui.Package#getCell_PopupMenu()
   * @model containment="true"
   * @generated
   */
  MPopupMenu getPopupMenu();

  /**
   * Sets the value of the '{@link uk.co.saiman.eclipse.model.ui.Cell#getPopupMenu <em>Popup Menu</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Popup Menu</em>' containment reference.
   * @see #getPopupMenu()
   * @generated
   */
  void setPopupMenu(MPopupMenu value);

  /**
   * Returns the value of the '<em><b>Expanded</b></em>' attribute.
   * The default value is <code>"false"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Expanded</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * True if the tree node is expanded in the UI presentation
   * <!-- end-model-doc -->
   * @return the value of the '<em>Expanded</em>' attribute.
   * @see #setExpanded(boolean)
   * @see uk.co.saiman.eclipse.model.ui.Package#getCell_Expanded()
   * @model default="false"
   * @generated
   */
  boolean isExpanded();

  /**
   * Sets the value of the '{@link uk.co.saiman.eclipse.model.ui.Cell#isExpanded <em>Expanded</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Expanded</em>' attribute.
   * @see #isExpanded()
   * @generated
   */
  void setExpanded(boolean value);

} // Cell
