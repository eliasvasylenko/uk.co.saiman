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
package uk.co.saiman.eclipse.model.ui.impl;

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
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.CellContribution;
import uk.co.saiman.eclipse.model.ui.Factory;
import uk.co.saiman.eclipse.model.ui.HandledCell;
import uk.co.saiman.eclipse.model.ui.Tree;
import uk.co.saiman.eclipse.ui.TransferFormat;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PackageImpl extends EPackageImpl implements uk.co.saiman.eclipse.model.ui.Package {
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
  private EClass cellContributionEClass = null;

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
  private EDataType mediaTypeEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType objectEDataType = null;

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
   * @see uk.co.saiman.eclipse.model.ui.Package#eNS_URI
   * @see #init()
   * @generated
   */
  private PackageImpl() {
    super(eNS_URI, Factory.eINSTANCE);
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
   * <p>This method is used to initialize {@link uk.co.saiman.eclipse.model.ui.Package#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static uk.co.saiman.eclipse.model.ui.Package init() {
    if (isInited) return (uk.co.saiman.eclipse.model.ui.Package)EPackage.Registry.INSTANCE.getEPackage(uk.co.saiman.eclipse.model.ui.Package.eNS_URI);

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
    EPackage.Registry.INSTANCE.put(uk.co.saiman.eclipse.model.ui.Package.eNS_URI, thePackage);
    return thePackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCell() {
    return cellEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCell_MediaTypes() {
    return (EAttribute)cellEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCell_Editable() {
    return (EAttribute)cellEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCell_Contributions() {
    return (EReference)cellEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCell_PopupMenu() {
    return (EReference)cellEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCell_TransferFormats() {
    return (EAttribute)cellEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTree() {
    return treeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTree_Editable() {
    return (EAttribute)treeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCellContribution() {
    return cellContributionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCellContribution_Parent() {
    return (EReference)cellContributionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getHandledCell() {
    return handledCellEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getMediaType() {
    return mediaTypeEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getObject() {
    return objectEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getTransferFormat() {
    return transferFormatEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Factory getFactory() {
    return (Factory)getEFactoryInstance();
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
    createEAttribute(cellEClass, CELL__MEDIA_TYPES);
    createEAttribute(cellEClass, CELL__EDITABLE);
    createEReference(cellEClass, CELL__CONTRIBUTIONS);
    createEReference(cellEClass, CELL__POPUP_MENU);
    createEAttribute(cellEClass, CELL__TRANSFER_FORMATS);

    treeEClass = createEClass(TREE);
    createEAttribute(treeEClass, TREE__EDITABLE);

    cellContributionEClass = createEClass(CELL_CONTRIBUTION);
    createEReference(cellContributionEClass, CELL_CONTRIBUTION__PARENT);

    handledCellEClass = createEClass(HANDLED_CELL);

    // Create data types
    mediaTypeEDataType = createEDataType(MEDIA_TYPE);
    objectEDataType = createEDataType(OBJECT);
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
    g1 = createEGenericType(theUiPackage.getContext());
    treeEClass.getEGenericSuperTypes().add(g1);
    g1 = createEGenericType(theApplicationPackage.getContribution());
    treeEClass.getEGenericSuperTypes().add(g1);
    g1 = createEGenericType(theUiPackage.getElementContainer());
    g2 = createEGenericType(this.getCell());
    g1.getETypeArguments().add(g2);
    treeEClass.getEGenericSuperTypes().add(g1);
    cellContributionEClass.getESuperTypes().add(theUiPackage.getContext());
    cellContributionEClass.getESuperTypes().add(theApplicationPackage.getContribution());
    handledCellEClass.getESuperTypes().add(this.getCell());
    handledCellEClass.getESuperTypes().add(theMenuPackage.getHandledItem());

    // Initialize classes, features, and operations; add parameters
    initEClass(cellEClass, Cell.class, "Cell", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCell_MediaTypes(), this.getMediaType(), "mediaTypes", null, 0, -1, Cell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCell_Editable(), ecorePackage.getEBoolean(), "editable", null, 0, 1, Cell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCell_Contributions(), this.getCellContribution(), this.getCellContribution_Parent(), "contributions", null, 0, -1, Cell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCell_PopupMenu(), theMenuPackage.getPopupMenu(), null, "popupMenu", null, 0, 1, Cell.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    g1 = createEGenericType(this.getTransferFormat());
    g2 = createEGenericType();
    g1.getETypeArguments().add(g2);
    EGenericType g3 = createEGenericType(this.getObject());
    g2.setEUpperBound(g3);
    initEAttribute(getCell_TransferFormats(), g1, "transferFormats", null, 0, -1, Cell.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(treeEClass, Tree.class, "Tree", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTree_Editable(), ecorePackage.getEBoolean(), "editable", null, 0, 1, Tree.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(cellContributionEClass, CellContribution.class, "CellContribution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCellContribution_Parent(), this.getCell(), this.getCell_Contributions(), "parent", null, 0, 1, CellContribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(handledCellEClass, HandledCell.class, "HandledCell", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    // Initialize data types
    initEDataType(mediaTypeEDataType, MediaType.class, "MediaType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(objectEDataType, Object.class, "Object", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(transferFormatEDataType, TransferFormat.class, "TransferFormat", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);
  }

} //PackageImpl
