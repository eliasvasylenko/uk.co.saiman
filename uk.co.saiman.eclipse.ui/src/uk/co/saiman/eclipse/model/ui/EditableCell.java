/**
 */
package uk.co.saiman.eclipse.model.ui;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Editable
 * Cell</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link uk.co.saiman.eclipse.model.ui.EditableCell#isEditing
 * <em>Editing</em>}</li>
 * </ul>
 *
 * @see uk.co.saiman.eclipse.model.ui.Package#getEditableCell()
 * @model
 * @generated
 */
public interface EditableCell extends Cell {
  /**
   * Returns the value of the '<em><b>Editing</b></em>' attribute. The default
   * value is <code>"false"</code>. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Editing</em>' attribute isn't clear, there really
   * should be more of a description here...
   * </p>
   * <!-- end-user-doc --> <!-- begin-model-doc --> Whether the cell is currently
   * in the editing mode. During editing, if the current edit state represents a
   * valid input, the result of applying the edit should be placed into the
   * transient data with the key of the cells context value. <!-- end-model-doc
   * -->
   * 
   * @return the value of the '<em>Editing</em>' attribute.
   * @see #setEditing(boolean)
   * @see uk.co.saiman.eclipse.model.ui.Package#getEditableCell_Editing()
   * @model default="false"
   * @generated
   */
  boolean isEditing();

  /**
   * Sets the value of the
   * '{@link uk.co.saiman.eclipse.model.ui.EditableCell#isEditing
   * <em>Editing</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Editing</em>' attribute.
   * @see #isEditing()
   * @generated
   */
  void setEditing(boolean value);

} // EditableCell
