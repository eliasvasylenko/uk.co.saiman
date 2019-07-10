/**
 */
package uk.co.saiman.eclipse.model.ui;

import org.eclipse.core.commands.ParameterizedCommand;

import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Handled Cell</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.MHandledCell#getCommand <em>Command</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.MHandledCell#getWbCommand <em>Wb Command</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.MHandledCell#getParameters <em>Parameters</em>}</li>
 * </ul>
 *
 * @see uk.co.saiman.eclipse.model.ui.MPackage#getHandledCell()
 * @model
 * @generated
 */
public interface MHandledCell extends MCell {
  /**
   * Returns the value of the '<em><b>Command</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * <p>
   * A reference to the Command associated with this item.
   * </p>
   * <!-- end-model-doc -->
   * @return the value of the '<em>Command</em>' reference.
   * @see #setCommand(MCommand)
   * @see uk.co.saiman.eclipse.model.ui.MPackage#getHandledCell_Command()
   * @model
   * @generated
   */
  MCommand getCommand();

  /**
   * Sets the value of the '{@link uk.co.saiman.eclipse.model.ui.MHandledCell#getCommand <em>Command</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Command</em>' reference.
   * @see #getCommand()
   * @generated
   */
  void setCommand(MCommand value);

  /**
   * Returns the value of the '<em><b>Wb Command</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * <p>
   * This is used for low level implementation and is not intended to be used by clients
   * </p>
   * @noreference
   * <!-- end-model-doc -->
   * @return the value of the '<em>Wb Command</em>' attribute.
   * @see #setWbCommand(ParameterizedCommand)
   * @see uk.co.saiman.eclipse.model.ui.MPackage#getHandledCell_WbCommand()
   * @model dataType="org.eclipse.e4.ui.model.application.commands.ParameterizedCommand" transient="true"
   * @generated
   */
  ParameterizedCommand getWbCommand();

  /**
   * Sets the value of the '{@link uk.co.saiman.eclipse.model.ui.MHandledCell#getWbCommand <em>Wb Command</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Wb Command</em>' attribute.
   * @see #getWbCommand()
   * @noreference
   * @generated
   */
  void setWbCommand(ParameterizedCommand value);

  /**
   * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.e4.ui.model.application.commands.MParameter}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * <p>
   * Defines the specific parameters to use when executing the command through this item.
   * </p>
   * <!-- end-model-doc -->
   * @return the value of the '<em>Parameters</em>' containment reference list.
   * @see uk.co.saiman.eclipse.model.ui.MPackage#getHandledCell_Parameters()
   * @model containment="true"
   * @generated
   */
  EList<MParameter> getParameters();

} // MHandledCell
