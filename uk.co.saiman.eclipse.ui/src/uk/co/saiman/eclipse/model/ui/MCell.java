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
 *   <li>{@link uk.co.saiman.eclipse.model.ui.MCell#getPopupMenu <em>Popup Menu</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.MCell#isExpanded <em>Expanded</em>}</li>
 * </ul>
 *
 * @see uk.co.saiman.eclipse.model.ui.MPackage#getCell()
 * @model
 * @generated
 */
public interface MCell extends MUILabel, MContext, MContribution, MElementContainer<MCell>, MHandlerContainer {
  /**
   * Returns the value of the '<em><b>Popup Menu</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Popup Menu</em>' containment reference.
   * @see #setPopupMenu(MPopupMenu)
   * @see uk.co.saiman.eclipse.model.ui.MPackage#getCell_PopupMenu()
   * @model containment="true"
   * @generated
   */
  MPopupMenu getPopupMenu();

  /**
   * Sets the value of the '{@link uk.co.saiman.eclipse.model.ui.MCell#getPopupMenu <em>Popup Menu</em>}' containment reference.
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
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * True if the tree node is expanded in the UI presentation
   * <!-- end-model-doc -->
   * @return the value of the '<em>Expanded</em>' attribute.
   * @see #setExpanded(boolean)
   * @see uk.co.saiman.eclipse.model.ui.MPackage#getCell_Expanded()
   * @model default="false"
   * @generated
   */
  boolean isExpanded();

  /**
   * Sets the value of the '{@link uk.co.saiman.eclipse.model.ui.MCell#isExpanded <em>Expanded</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Expanded</em>' attribute.
   * @see #isExpanded()
   * @generated
   */
  void setExpanded(boolean value);

} // MCell
