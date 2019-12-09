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
package uk.co.saiman.eclipse.model.ui.impl;

import org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl;

import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;

import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;

import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import uk.co.saiman.data.format.MediaType;

import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.model.ui.MEditableCell;
import uk.co.saiman.eclipse.model.ui.MHandledCell;
import uk.co.saiman.eclipse.model.ui.MFactory;
import uk.co.saiman.eclipse.model.ui.MPackage;
import uk.co.saiman.eclipse.model.ui.MTree;

import uk.co.saiman.eclipse.ui.TransferFormat;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PackageImpl extends EPackageImpl implements MPackage {
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass cellEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass treeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass handledCellEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass editableCellEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType mediaTypeEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType transferFormatEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see uk.co.saiman.eclipse.model.ui.MPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private PackageImpl() {
    super(eNS_URI, MFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link MPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static MPackage init() {
    if (isInited) return (MPackage)EPackage.Registry.INSTANCE.getEPackage(MPackage.eNS_URI);

    // Obtain or create and register package
    Object registeredPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    PackageImpl thePackage = registeredPackage instanceof PackageImpl ? (PackageImpl)registeredPackage : new PackageImpl();

    isInited = true;

    // Initialize simple dependencies
    ApplicationPackageImpl.eINSTANCE.eClass();

    // Create package meta-data objects
    thePackage.createPackageContents();

    // Initialize created meta-data
    thePackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    thePackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(MPackage.eNS_URI, thePackage);
    return thePackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getCell() {
    return cellEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCell_PopupMenu() {
    return (EReference)cellEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getCell_Expanded() {
    return (EAttribute)cellEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getTree() {
    return treeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTree_Editable() {
    return (EAttribute)treeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getHandledCell() {
    return handledCellEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getHandledCell_Command() {
    return (EReference)handledCellEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @noreference
   * @generated
   */
  @Override
  public EAttribute getHandledCell_WbCommand() {
    return (EAttribute)handledCellEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getHandledCell_Parameters() {
    return (EReference)handledCellEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getEditableCell() {
    return editableCellEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getEditableCell_Editing() {
    return (EAttribute)editableCellEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EDataType getMediaType() {
    return mediaTypeEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EDataType getTransferFormat() {
    return transferFormatEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MFactory getFactory() {
    return (MFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents() {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    cellEClass = createEClass(CELL);
    createEReference(cellEClass, CELL__POPUP_MENU);
    createEAttribute(cellEClass, CELL__EXPANDED);

    treeEClass = createEClass(TREE);
    createEAttribute(treeEClass, TREE__EDITABLE);

    handledCellEClass = createEClass(HANDLED_CELL);
    createEReference(handledCellEClass, HANDLED_CELL__COMMAND);
    createEAttribute(handledCellEClass, HANDLED_CELL__WB_COMMAND);
    createEReference(handledCellEClass, HANDLED_CELL__PARAMETERS);

    editableCellEClass = createEClass(EDITABLE_CELL);
    createEAttribute(editableCellEClass, EDITABLE_CELL__EDITING);

    // Create data types
    mediaTypeEDataType = createEDataType(MEDIA_TYPE);
    transferFormatEDataType = createEDataType(TRANSFER_FORMAT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents() {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    UiPackageImpl theUiPackage = (UiPackageImpl)EPackage.Registry.INSTANCE.getEPackage(UiPackageImpl.eNS_URI);
    ApplicationPackageImpl theApplicationPackage = (ApplicationPackageImpl)EPackage.Registry.INSTANCE.getEPackage(ApplicationPackageImpl.eNS_URI);
    CommandsPackageImpl theCommandsPackage = (CommandsPackageImpl)EPackage.Registry.INSTANCE.getEPackage(CommandsPackageImpl.eNS_URI);
    MenuPackageImpl theMenuPackage = (MenuPackageImpl)EPackage.Registry.INSTANCE.getEPackage(MenuPackageImpl.eNS_URI);

    // Create type parameters
    addETypeParameter(transferFormatEDataType, "T");

    // Set bounds for type parameters

    // Add supertypes to classes
    EGenericType g1 = createEGenericType(theUiPackage.getUILabel());
    cellEClass.getEGenericSuperTypes().add(g1);
    g1 = createEGenericType(theUiPackage.getContext());
    cellEClass.getEGenericSuperTypes().add(g1);
    g1 = createEGenericType(theApplicationPackage.getContribution());
    cellEClass.getEGenericSuperTypes().add(g1);
    g1 = createEGenericType(theUiPackage.getElementContainer());
    EGenericType g2 = createEGenericType(this.getCell());
    g1.getETypeArguments().add(g2);
    cellEClass.getEGenericSuperTypes().add(g1);
    g1 = createEGenericType(theCommandsPackage.getHandlerContainer());
    cellEClass.getEGenericSuperTypes().add(g1);
    g1 = createEGenericType(theUiPackage.getContext());
    treeEClass.getEGenericSuperTypes().add(g1);
    g1 = createEGenericType(theApplicationPackage.getContribution());
    treeEClass.getEGenericSuperTypes().add(g1);
    g1 = createEGenericType(theUiPackage.getElementContainer());
    g2 = createEGenericType(this.getCell());
    g1.getETypeArguments().add(g2);
    treeEClass.getEGenericSuperTypes().add(g1);
    g1 = createEGenericType(theCommandsPackage.getHandlerContainer());
    treeEClass.getEGenericSuperTypes().add(g1);
    handledCellEClass.getESuperTypes().add(this.getCell());
    editableCellEClass.getESuperTypes().add(this.getCell());

    // Initialize classes, features, and operations; add parameters
    initEClass(cellEClass, MCell.class, "Cell", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCell_PopupMenu(), theMenuPackage.getPopupMenu(), null, "popupMenu", null, 0, 1, MCell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCell_Expanded(), ecorePackage.getEBoolean(), "expanded", "false", 0, 1, MCell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(treeEClass, MTree.class, "Tree", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTree_Editable(), ecorePackage.getEBoolean(), "editable", null, 0, 1, MTree.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(handledCellEClass, MHandledCell.class, "HandledCell", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getHandledCell_Command(), theCommandsPackage.getCommand(), null, "command", null, 0, 1, MHandledCell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getHandledCell_WbCommand(), theCommandsPackage.getParameterizedCommand(), "wbCommand", null, 0, 1, MHandledCell.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getHandledCell_Parameters(), theCommandsPackage.getParameter(), null, "parameters", null, 0, -1, MHandledCell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(editableCellEClass, MEditableCell.class, "EditableCell", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEditableCell_Editing(), ecorePackage.getEBoolean(), "editing", "false", 0, 1, MEditableCell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize data types
    initEDataType(mediaTypeEDataType, MediaType.class, "MediaType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(transferFormatEDataType, TransferFormat.class, "TransferFormat", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);
  }

} //PackageImpl
