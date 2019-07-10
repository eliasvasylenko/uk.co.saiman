/**
 */
package uk.co.saiman.eclipse.model.ui.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import uk.co.saiman.eclipse.model.ui.MEditableCell;
import uk.co.saiman.eclipse.model.ui.MPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Editable Cell</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.EditableCellImpl#isEditing <em>Editing</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EditableCellImpl extends CellImpl implements MEditableCell {
  /**
   * The default value of the '{@link #isEditing() <em>Editing</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isEditing()
   * @generated
   * @ordered
   */
  protected static final boolean EDITING_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isEditing() <em>Editing</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isEditing()
   * @generated
   * @ordered
   */
  protected boolean editing = EDITING_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EditableCellImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return MPackage.Literals.EDITABLE_CELL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isEditing() {
    return editing;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEditing(boolean newEditing) {
    boolean oldEditing = editing;
    editing = newEditing;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MPackage.EDITABLE_CELL__EDITING, oldEditing, editing));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case MPackage.EDITABLE_CELL__EDITING:
        return isEditing();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue) {
    switch (featureID) {
      case MPackage.EDITABLE_CELL__EDITING:
        setEditing((Boolean)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID) {
    switch (featureID) {
      case MPackage.EDITABLE_CELL__EDITING:
        setEditing(EDITING_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID) {
    switch (featureID) {
      case MPackage.EDITABLE_CELL__EDITING:
        return editing != EDITING_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString() {
    if (eIsProxy()) return super.toString();

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (editing: ");
    result.append(editing);
    result.append(')');
    return result.toString();
  }

} //EditableCellImpl
