/**
 */
package uk.co.saiman.eclipse.model.ui;

import org.eclipse.e4.ui.model.application.MContribution;

import org.eclipse.e4.ui.model.application.commands.MHandlerContainer;

import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tree</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.MTree#isEditable <em>Editable</em>}</li>
 * </ul>
 *
 * @see uk.co.saiman.eclipse.model.ui.MPackage#getTree()
 * @model
 * @generated
 */
public interface MTree extends MContext, MContribution, MElementContainer<MCell>, MHandlerContainer {
  /**
   * Returns the value of the '<em><b>Editable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Editable</em>' attribute.
   * @see #setEditable(boolean)
   * @see uk.co.saiman.eclipse.model.ui.MPackage#getTree_Editable()
   * @model
   * @generated
   */
  boolean isEditable();

  /**
   * Sets the value of the '{@link uk.co.saiman.eclipse.model.ui.MTree#isEditable <em>Editable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Editable</em>' attribute.
   * @see #isEditable()
   * @generated
   */
  void setEditable(boolean value);

} // MTree
