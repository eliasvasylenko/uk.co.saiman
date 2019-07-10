/**
 */
package uk.co.saiman.eclipse.model.ui;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see uk.co.saiman.eclipse.model.ui.MPackage
 * @generated
 */
public interface MFactory extends EFactory {
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  MFactory eINSTANCE = uk.co.saiman.eclipse.model.ui.impl.FactoryImpl.init();

  /**
   * Returns a new object of class '<em>Cell</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Cell</em>'.
   * @generated
   */
  MCell createCell();

  /**
   * Returns a new object of class '<em>Tree</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Tree</em>'.
   * @generated
   */
  MTree createTree();

  /**
   * Returns a new object of class '<em>Handled Cell</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Handled Cell</em>'.
   * @generated
   */
  MHandledCell createHandledCell();

  /**
   * Returns a new object of class '<em>Editable Cell</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Editable Cell</em>'.
   * @generated
   */
  MEditableCell createEditableCell();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  MPackage getPackage();

} //MFactory
