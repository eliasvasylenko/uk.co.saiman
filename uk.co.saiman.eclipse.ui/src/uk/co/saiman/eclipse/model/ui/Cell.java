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
import org.eclipse.emf.common.util.EList;
import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.eclipse.ui.TransferFormat;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Cell</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.Cell#getMediaTypes <em>Media Types</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.Cell#isEditable <em>Editable</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.Cell#getContributions <em>Contributions</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.Cell#getPopupMenu <em>Popup Menu</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.Cell#getTransferFormats <em>Transfer Formats</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.Cell#getContextValue <em>Context Value</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.Cell#isOptional <em>Optional</em>}</li>
 * </ul>
 *
 * @see uk.co.saiman.eclipse.model.ui.Package#getCell()
 * @model
 * @generated
 */
public interface Cell extends MUILabel, MContext, MContribution, MElementContainer<Cell>, MHandlerContainer {
  /**
   * Returns the value of the '<em><b>Media Types</b></em>' attribute list.
   * The list contents are of type {@link uk.co.saiman.data.format.MediaType}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Media Types</em>' attribute list.
   * @see uk.co.saiman.eclipse.model.ui.Package#getCell_MediaTypes()
   * @model dataType="uk.co.saiman.eclipse.model.ui.MediaType"
   * @generated
   */
  EList<MediaType> getMediaTypes();

  /**
   * Returns the value of the '<em><b>Editable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Editable</em>' attribute.
   * @see #setEditable(boolean)
   * @see uk.co.saiman.eclipse.model.ui.Package#getCell_Editable()
   * @model
   * @generated
   */
  boolean isEditable();

  /**
   * Sets the value of the '{@link uk.co.saiman.eclipse.model.ui.Cell#isEditable <em>Editable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Editable</em>' attribute.
   * @see #isEditable()
   * @generated
   */
  void setEditable(boolean value);

  /**
   * Returns the value of the '<em><b>Contributions</b></em>' containment reference list.
   * The list contents are of type {@link uk.co.saiman.eclipse.model.ui.CellContribution}.
   * It is bidirectional and its opposite is '{@link uk.co.saiman.eclipse.model.ui.CellContribution#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Contributions</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Contributions</em>' containment reference list.
   * @see uk.co.saiman.eclipse.model.ui.Package#getCell_Contributions()
   * @see uk.co.saiman.eclipse.model.ui.CellContribution#getParent
   * @model opposite="parent" containment="true"
   * @generated
   */
  EList<CellContribution> getContributions();

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
   * Returns the value of the '<em><b>Transfer Formats</b></em>' attribute list.
   * The list contents are of type {@link uk.co.saiman.eclipse.ui.TransferFormat}<code>&lt;?&gt;</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Transfer Formats</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Transfer Formats</em>' attribute list.
   * @see uk.co.saiman.eclipse.model.ui.Package#getCell_TransferFormats()
   * @model dataType="uk.co.saiman.eclipse.model.ui.TransferFormat&lt;?&gt;" transient="true" derived="true"
   * @generated
   */
  EList<TransferFormat<?>> getTransferFormats();

  /**
   * Returns the value of the '<em><b>Context Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Context Value</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Context Value</em>' attribute.
   * @see #setContextValue(String)
   * @see uk.co.saiman.eclipse.model.ui.Package#getCell_ContextValue()
   * @model
   * @generated
   */
  String getContextValue();

  /**
   * Sets the value of the '{@link uk.co.saiman.eclipse.model.ui.Cell#getContextValue <em>Context Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Context Value</em>' attribute.
   * @see #getContextValue()
   * @generated
   */
  void setContextValue(String value);

  /**
   * Returns the value of the '<em><b>Optional</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Optional</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Optional</em>' attribute.
   * @see #setOptional(boolean)
   * @see uk.co.saiman.eclipse.model.ui.Package#getCell_Optional()
   * @model
   * @generated
   */
  boolean isOptional();

  /**
   * Sets the value of the '{@link uk.co.saiman.eclipse.model.ui.Cell#isOptional <em>Optional</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Optional</em>' attribute.
   * @see #isOptional()
   * @generated
   */
  void setOptional(boolean value);

} // Cell
