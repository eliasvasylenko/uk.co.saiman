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

import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see uk.co.saiman.eclipse.model.ui.Factory
 * @model kind="package"
 * @generated
 */
public interface Package extends EPackage {
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "ui";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.saiman.co.uk/eclipse/2018/UISaiman/ui";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "saiman.ui";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  Package eINSTANCE = uk.co.saiman.eclipse.model.ui.impl.PackageImpl.init();

  /**
   * The meta object id for the '{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl <em>Cell</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see uk.co.saiman.eclipse.model.ui.impl.CellImpl
   * @see uk.co.saiman.eclipse.model.ui.impl.PackageImpl#getCell()
   * @generated
   */
  int CELL = 0;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__LABEL = UiPackageImpl.UI_LABEL__LABEL;

  /**
   * The feature id for the '<em><b>Icon URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__ICON_URI = UiPackageImpl.UI_LABEL__ICON_URI;

  /**
   * The feature id for the '<em><b>Tooltip</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__TOOLTIP = UiPackageImpl.UI_LABEL__TOOLTIP;

  /**
   * The feature id for the '<em><b>Localized Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__LOCALIZED_LABEL = UiPackageImpl.UI_LABEL__LOCALIZED_LABEL;

  /**
   * The feature id for the '<em><b>Localized Tooltip</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__LOCALIZED_TOOLTIP = UiPackageImpl.UI_LABEL__LOCALIZED_TOOLTIP;

  /**
   * The feature id for the '<em><b>Context</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__CONTEXT = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Variables</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__VARIABLES = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Properties</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__PROPERTIES = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Element Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__ELEMENT_ID = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Persisted State</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__PERSISTED_STATE = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Tags</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__TAGS = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Contributor URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__CONTRIBUTOR_URI = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Transient Data</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__TRANSIENT_DATA = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Contribution URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__CONTRIBUTION_URI = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 8;

  /**
   * The feature id for the '<em><b>Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__OBJECT = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 9;

  /**
   * The feature id for the '<em><b>Widget</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__WIDGET = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 10;

  /**
   * The feature id for the '<em><b>Renderer</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__RENDERER = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 11;

  /**
   * The feature id for the '<em><b>To Be Rendered</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__TO_BE_RENDERED = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 12;

  /**
   * The feature id for the '<em><b>On Top</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__ON_TOP = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 13;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__VISIBLE = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 14;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__PARENT = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 15;

  /**
   * The feature id for the '<em><b>Container Data</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__CONTAINER_DATA = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 16;

  /**
   * The feature id for the '<em><b>Cur Shared Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__CUR_SHARED_REF = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 17;

  /**
   * The feature id for the '<em><b>Visible When</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__VISIBLE_WHEN = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 18;

  /**
   * The feature id for the '<em><b>Accessibility Phrase</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__ACCESSIBILITY_PHRASE = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 19;

  /**
   * The feature id for the '<em><b>Localized Accessibility Phrase</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__LOCALIZED_ACCESSIBILITY_PHRASE = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 20;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__CHILDREN = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 21;

  /**
   * The feature id for the '<em><b>Selected Element</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__SELECTED_ELEMENT = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 22;

  /**
   * The feature id for the '<em><b>Handlers</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int CELL__HANDLERS = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 23;

  /**
   * The feature id for the '<em><b>Media Types</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CELL__MEDIA_TYPES = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 24;

  /**
   * The feature id for the '<em><b>Popup Menu</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CELL__POPUP_MENU = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 25;

  /**
   * The feature id for the '<em><b>Transfer Formats</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CELL__TRANSFER_FORMATS = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 26;

  /**
   * The feature id for the '<em><b>Context Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CELL__CONTEXT_VALUE = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 27;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CELL__EXPANDED = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 28;

  /**
   * The feature id for the '<em><b>Nullable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CELL__NULLABLE = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 29;

  /**
   * The number of structural features of the '<em>Cell</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CELL_FEATURE_COUNT = UiPackageImpl.UI_LABEL_FEATURE_COUNT + 30;

  /**
   * The operation id for the '<em>Update Localization</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.1
   * @generated
   * @ordered
   */
  int CELL___UPDATE_LOCALIZATION = UiPackageImpl.UI_LABEL___UPDATE_LOCALIZATION;

  /**
   * The number of operations of the '<em>Cell</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CELL_OPERATION_COUNT = UiPackageImpl.UI_LABEL_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl <em>Tree</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see uk.co.saiman.eclipse.model.ui.impl.TreeImpl
   * @see uk.co.saiman.eclipse.model.ui.impl.PackageImpl#getTree()
   * @generated
   */
  int TREE = 1;

  /**
   * The feature id for the '<em><b>Context</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__CONTEXT = UiPackageImpl.CONTEXT__CONTEXT;

  /**
   * The feature id for the '<em><b>Variables</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__VARIABLES = UiPackageImpl.CONTEXT__VARIABLES;

  /**
   * The feature id for the '<em><b>Properties</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__PROPERTIES = UiPackageImpl.CONTEXT__PROPERTIES;

  /**
   * The feature id for the '<em><b>Element Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__ELEMENT_ID = UiPackageImpl.CONTEXT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Persisted State</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__PERSISTED_STATE = UiPackageImpl.CONTEXT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Tags</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__TAGS = UiPackageImpl.CONTEXT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Contributor URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__CONTRIBUTOR_URI = UiPackageImpl.CONTEXT_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Transient Data</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__TRANSIENT_DATA = UiPackageImpl.CONTEXT_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Contribution URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__CONTRIBUTION_URI = UiPackageImpl.CONTEXT_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__OBJECT = UiPackageImpl.CONTEXT_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Widget</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__WIDGET = UiPackageImpl.CONTEXT_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Renderer</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__RENDERER = UiPackageImpl.CONTEXT_FEATURE_COUNT + 8;

  /**
   * The feature id for the '<em><b>To Be Rendered</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__TO_BE_RENDERED = UiPackageImpl.CONTEXT_FEATURE_COUNT + 9;

  /**
   * The feature id for the '<em><b>On Top</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__ON_TOP = UiPackageImpl.CONTEXT_FEATURE_COUNT + 10;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__VISIBLE = UiPackageImpl.CONTEXT_FEATURE_COUNT + 11;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__PARENT = UiPackageImpl.CONTEXT_FEATURE_COUNT + 12;

  /**
   * The feature id for the '<em><b>Container Data</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__CONTAINER_DATA = UiPackageImpl.CONTEXT_FEATURE_COUNT + 13;

  /**
   * The feature id for the '<em><b>Cur Shared Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__CUR_SHARED_REF = UiPackageImpl.CONTEXT_FEATURE_COUNT + 14;

  /**
   * The feature id for the '<em><b>Visible When</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__VISIBLE_WHEN = UiPackageImpl.CONTEXT_FEATURE_COUNT + 15;

  /**
   * The feature id for the '<em><b>Accessibility Phrase</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__ACCESSIBILITY_PHRASE = UiPackageImpl.CONTEXT_FEATURE_COUNT + 16;

  /**
   * The feature id for the '<em><b>Localized Accessibility Phrase</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__LOCALIZED_ACCESSIBILITY_PHRASE = UiPackageImpl.CONTEXT_FEATURE_COUNT + 17;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__CHILDREN = UiPackageImpl.CONTEXT_FEATURE_COUNT + 18;

  /**
   * The feature id for the '<em><b>Selected Element</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__SELECTED_ELEMENT = UiPackageImpl.CONTEXT_FEATURE_COUNT + 19;

  /**
   * The feature id for the '<em><b>Handlers</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int TREE__HANDLERS = UiPackageImpl.CONTEXT_FEATURE_COUNT + 20;

  /**
   * The feature id for the '<em><b>Editable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TREE__EDITABLE = UiPackageImpl.CONTEXT_FEATURE_COUNT + 21;

  /**
   * The number of structural features of the '<em>Tree</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TREE_FEATURE_COUNT = UiPackageImpl.CONTEXT_FEATURE_COUNT + 22;

  /**
   * The operation id for the '<em>Update Localization</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.1
   * @generated
   * @ordered
   */
  int TREE___UPDATE_LOCALIZATION = UiPackageImpl.CONTEXT_OPERATION_COUNT + 0;

  /**
   * The number of operations of the '<em>Tree</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TREE_OPERATION_COUNT = UiPackageImpl.CONTEXT_OPERATION_COUNT + 1;

  /**
   * The meta object id for the '{@link uk.co.saiman.eclipse.model.ui.impl.HandledCellImpl <em>Handled Cell</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see uk.co.saiman.eclipse.model.ui.impl.HandledCellImpl
   * @see uk.co.saiman.eclipse.model.ui.impl.PackageImpl#getHandledCell()
   * @generated
   */
  int HANDLED_CELL = 2;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__LABEL = CELL__LABEL;

  /**
   * The feature id for the '<em><b>Icon URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__ICON_URI = CELL__ICON_URI;

  /**
   * The feature id for the '<em><b>Tooltip</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__TOOLTIP = CELL__TOOLTIP;

  /**
   * The feature id for the '<em><b>Localized Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__LOCALIZED_LABEL = CELL__LOCALIZED_LABEL;

  /**
   * The feature id for the '<em><b>Localized Tooltip</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__LOCALIZED_TOOLTIP = CELL__LOCALIZED_TOOLTIP;

  /**
   * The feature id for the '<em><b>Context</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__CONTEXT = CELL__CONTEXT;

  /**
   * The feature id for the '<em><b>Variables</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__VARIABLES = CELL__VARIABLES;

  /**
   * The feature id for the '<em><b>Properties</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__PROPERTIES = CELL__PROPERTIES;

  /**
   * The feature id for the '<em><b>Element Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__ELEMENT_ID = CELL__ELEMENT_ID;

  /**
   * The feature id for the '<em><b>Persisted State</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__PERSISTED_STATE = CELL__PERSISTED_STATE;

  /**
   * The feature id for the '<em><b>Tags</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__TAGS = CELL__TAGS;

  /**
   * The feature id for the '<em><b>Contributor URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__CONTRIBUTOR_URI = CELL__CONTRIBUTOR_URI;

  /**
   * The feature id for the '<em><b>Transient Data</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__TRANSIENT_DATA = CELL__TRANSIENT_DATA;

  /**
   * The feature id for the '<em><b>Contribution URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__CONTRIBUTION_URI = CELL__CONTRIBUTION_URI;

  /**
   * The feature id for the '<em><b>Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__OBJECT = CELL__OBJECT;

  /**
   * The feature id for the '<em><b>Widget</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__WIDGET = CELL__WIDGET;

  /**
   * The feature id for the '<em><b>Renderer</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__RENDERER = CELL__RENDERER;

  /**
   * The feature id for the '<em><b>To Be Rendered</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__TO_BE_RENDERED = CELL__TO_BE_RENDERED;

  /**
   * The feature id for the '<em><b>On Top</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__ON_TOP = CELL__ON_TOP;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__VISIBLE = CELL__VISIBLE;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__PARENT = CELL__PARENT;

  /**
   * The feature id for the '<em><b>Container Data</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__CONTAINER_DATA = CELL__CONTAINER_DATA;

  /**
   * The feature id for the '<em><b>Cur Shared Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__CUR_SHARED_REF = CELL__CUR_SHARED_REF;

  /**
   * The feature id for the '<em><b>Visible When</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__VISIBLE_WHEN = CELL__VISIBLE_WHEN;

  /**
   * The feature id for the '<em><b>Accessibility Phrase</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__ACCESSIBILITY_PHRASE = CELL__ACCESSIBILITY_PHRASE;

  /**
   * The feature id for the '<em><b>Localized Accessibility Phrase</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__LOCALIZED_ACCESSIBILITY_PHRASE = CELL__LOCALIZED_ACCESSIBILITY_PHRASE;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__CHILDREN = CELL__CHILDREN;

  /**
   * The feature id for the '<em><b>Selected Element</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__SELECTED_ELEMENT = CELL__SELECTED_ELEMENT;

  /**
   * The feature id for the '<em><b>Handlers</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int HANDLED_CELL__HANDLERS = CELL__HANDLERS;

  /**
   * The feature id for the '<em><b>Media Types</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HANDLED_CELL__MEDIA_TYPES = CELL__MEDIA_TYPES;

  /**
   * The feature id for the '<em><b>Popup Menu</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HANDLED_CELL__POPUP_MENU = CELL__POPUP_MENU;

  /**
   * The feature id for the '<em><b>Transfer Formats</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HANDLED_CELL__TRANSFER_FORMATS = CELL__TRANSFER_FORMATS;

  /**
   * The feature id for the '<em><b>Context Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HANDLED_CELL__CONTEXT_VALUE = CELL__CONTEXT_VALUE;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HANDLED_CELL__EXPANDED = CELL__EXPANDED;

  /**
   * The feature id for the '<em><b>Nullable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HANDLED_CELL__NULLABLE = CELL__NULLABLE;

  /**
   * The feature id for the '<em><b>Command</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HANDLED_CELL__COMMAND = CELL_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Wb Command</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HANDLED_CELL__WB_COMMAND = CELL_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HANDLED_CELL__PARAMETERS = CELL_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Handled Cell</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HANDLED_CELL_FEATURE_COUNT = CELL_FEATURE_COUNT + 3;

  /**
   * The operation id for the '<em>Update Localization</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.1
   * @generated
   * @ordered
   */
  int HANDLED_CELL___UPDATE_LOCALIZATION = CELL___UPDATE_LOCALIZATION;

  /**
   * The number of operations of the '<em>Handled Cell</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HANDLED_CELL_OPERATION_COUNT = CELL_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link uk.co.saiman.eclipse.model.ui.impl.EditableCellImpl <em>Editable Cell</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see uk.co.saiman.eclipse.model.ui.impl.EditableCellImpl
   * @see uk.co.saiman.eclipse.model.ui.impl.PackageImpl#getEditableCell()
   * @generated
   */
  int EDITABLE_CELL = 3;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__LABEL = CELL__LABEL;

  /**
   * The feature id for the '<em><b>Icon URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__ICON_URI = CELL__ICON_URI;

  /**
   * The feature id for the '<em><b>Tooltip</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__TOOLTIP = CELL__TOOLTIP;

  /**
   * The feature id for the '<em><b>Localized Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__LOCALIZED_LABEL = CELL__LOCALIZED_LABEL;

  /**
   * The feature id for the '<em><b>Localized Tooltip</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__LOCALIZED_TOOLTIP = CELL__LOCALIZED_TOOLTIP;

  /**
   * The feature id for the '<em><b>Context</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__CONTEXT = CELL__CONTEXT;

  /**
   * The feature id for the '<em><b>Variables</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__VARIABLES = CELL__VARIABLES;

  /**
   * The feature id for the '<em><b>Properties</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__PROPERTIES = CELL__PROPERTIES;

  /**
   * The feature id for the '<em><b>Element Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__ELEMENT_ID = CELL__ELEMENT_ID;

  /**
   * The feature id for the '<em><b>Persisted State</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__PERSISTED_STATE = CELL__PERSISTED_STATE;

  /**
   * The feature id for the '<em><b>Tags</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__TAGS = CELL__TAGS;

  /**
   * The feature id for the '<em><b>Contributor URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__CONTRIBUTOR_URI = CELL__CONTRIBUTOR_URI;

  /**
   * The feature id for the '<em><b>Transient Data</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__TRANSIENT_DATA = CELL__TRANSIENT_DATA;

  /**
   * The feature id for the '<em><b>Contribution URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__CONTRIBUTION_URI = CELL__CONTRIBUTION_URI;

  /**
   * The feature id for the '<em><b>Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__OBJECT = CELL__OBJECT;

  /**
   * The feature id for the '<em><b>Widget</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__WIDGET = CELL__WIDGET;

  /**
   * The feature id for the '<em><b>Renderer</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__RENDERER = CELL__RENDERER;

  /**
   * The feature id for the '<em><b>To Be Rendered</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__TO_BE_RENDERED = CELL__TO_BE_RENDERED;

  /**
   * The feature id for the '<em><b>On Top</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__ON_TOP = CELL__ON_TOP;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__VISIBLE = CELL__VISIBLE;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__PARENT = CELL__PARENT;

  /**
   * The feature id for the '<em><b>Container Data</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__CONTAINER_DATA = CELL__CONTAINER_DATA;

  /**
   * The feature id for the '<em><b>Cur Shared Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__CUR_SHARED_REF = CELL__CUR_SHARED_REF;

  /**
   * The feature id for the '<em><b>Visible When</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__VISIBLE_WHEN = CELL__VISIBLE_WHEN;

  /**
   * The feature id for the '<em><b>Accessibility Phrase</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__ACCESSIBILITY_PHRASE = CELL__ACCESSIBILITY_PHRASE;

  /**
   * The feature id for the '<em><b>Localized Accessibility Phrase</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__LOCALIZED_ACCESSIBILITY_PHRASE = CELL__LOCALIZED_ACCESSIBILITY_PHRASE;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__CHILDREN = CELL__CHILDREN;

  /**
   * The feature id for the '<em><b>Selected Element</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__SELECTED_ELEMENT = CELL__SELECTED_ELEMENT;

  /**
   * The feature id for the '<em><b>Handlers</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__HANDLERS = CELL__HANDLERS;

  /**
   * The feature id for the '<em><b>Media Types</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__MEDIA_TYPES = CELL__MEDIA_TYPES;

  /**
   * The feature id for the '<em><b>Popup Menu</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__POPUP_MENU = CELL__POPUP_MENU;

  /**
   * The feature id for the '<em><b>Transfer Formats</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__TRANSFER_FORMATS = CELL__TRANSFER_FORMATS;

  /**
   * The feature id for the '<em><b>Context Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__CONTEXT_VALUE = CELL__CONTEXT_VALUE;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__EXPANDED = CELL__EXPANDED;

  /**
   * The feature id for the '<em><b>Nullable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__NULLABLE = CELL__NULLABLE;

  /**
   * The feature id for the '<em><b>Editing</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDITABLE_CELL__EDITING = CELL_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Editable Cell</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDITABLE_CELL_FEATURE_COUNT = CELL_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Update Localization</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.1
   * @generated
   * @ordered
   */
  int EDITABLE_CELL___UPDATE_LOCALIZATION = CELL___UPDATE_LOCALIZATION;

  /**
   * The number of operations of the '<em>Editable Cell</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDITABLE_CELL_OPERATION_COUNT = CELL_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '<em>Media Type</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see uk.co.saiman.data.format.MediaType
   * @see uk.co.saiman.eclipse.model.ui.impl.PackageImpl#getMediaType()
   * @generated
   */
  int MEDIA_TYPE = 4;


  /**
   * The meta object id for the '<em>Transfer Format</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see uk.co.saiman.eclipse.ui.TransferFormat
   * @see uk.co.saiman.eclipse.model.ui.impl.PackageImpl#getTransferFormat()
   * @generated
   */
  int TRANSFER_FORMAT = 5;


  /**
   * Returns the meta object for class '{@link uk.co.saiman.eclipse.model.ui.Cell <em>Cell</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Cell</em>'.
   * @see uk.co.saiman.eclipse.model.ui.Cell
   * @generated
   */
  EClass getCell();

  /**
   * Returns the meta object for the attribute list '{@link uk.co.saiman.eclipse.model.ui.Cell#getMediaTypes <em>Media Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Media Types</em>'.
   * @see uk.co.saiman.eclipse.model.ui.Cell#getMediaTypes()
   * @see #getCell()
   * @generated
   */
  EAttribute getCell_MediaTypes();

  /**
   * Returns the meta object for the containment reference '{@link uk.co.saiman.eclipse.model.ui.Cell#getPopupMenu <em>Popup Menu</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Popup Menu</em>'.
   * @see uk.co.saiman.eclipse.model.ui.Cell#getPopupMenu()
   * @see #getCell()
   * @generated
   */
  EReference getCell_PopupMenu();

  /**
   * Returns the meta object for the attribute list '{@link uk.co.saiman.eclipse.model.ui.Cell#getTransferFormats <em>Transfer Formats</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Transfer Formats</em>'.
   * @see uk.co.saiman.eclipse.model.ui.Cell#getTransferFormats()
   * @see #getCell()
   * @generated
   */
  EAttribute getCell_TransferFormats();

  /**
   * Returns the meta object for the attribute '{@link uk.co.saiman.eclipse.model.ui.Cell#getContextValue <em>Context Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Context Value</em>'.
   * @see uk.co.saiman.eclipse.model.ui.Cell#getContextValue()
   * @see #getCell()
   * @generated
   */
  EAttribute getCell_ContextValue();

  /**
   * Returns the meta object for the attribute '{@link uk.co.saiman.eclipse.model.ui.Cell#isExpanded <em>Expanded</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Expanded</em>'.
   * @see uk.co.saiman.eclipse.model.ui.Cell#isExpanded()
   * @see #getCell()
   * @generated
   */
  EAttribute getCell_Expanded();

  /**
   * Returns the meta object for the attribute '{@link uk.co.saiman.eclipse.model.ui.Cell#isNullable <em>Nullable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Nullable</em>'.
   * @see uk.co.saiman.eclipse.model.ui.Cell#isNullable()
   * @see #getCell()
   * @generated
   */
  EAttribute getCell_Nullable();

  /**
   * Returns the meta object for class '{@link uk.co.saiman.eclipse.model.ui.Tree <em>Tree</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Tree</em>'.
   * @see uk.co.saiman.eclipse.model.ui.Tree
   * @generated
   */
  EClass getTree();

  /**
   * Returns the meta object for the attribute '{@link uk.co.saiman.eclipse.model.ui.Tree#isEditable <em>Editable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Editable</em>'.
   * @see uk.co.saiman.eclipse.model.ui.Tree#isEditable()
   * @see #getTree()
   * @generated
   */
  EAttribute getTree_Editable();

  /**
   * Returns the meta object for class '{@link uk.co.saiman.eclipse.model.ui.HandledCell <em>Handled Cell</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Handled Cell</em>'.
   * @see uk.co.saiman.eclipse.model.ui.HandledCell
   * @generated
   */
  EClass getHandledCell();

  /**
   * Returns the meta object for the reference '{@link uk.co.saiman.eclipse.model.ui.HandledCell#getCommand <em>Command</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Command</em>'.
   * @see uk.co.saiman.eclipse.model.ui.HandledCell#getCommand()
   * @see #getHandledCell()
   * @generated
   */
  EReference getHandledCell_Command();

  /**
   * Returns the meta object for the attribute '{@link uk.co.saiman.eclipse.model.ui.HandledCell#getWbCommand <em>Wb Command</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Wb Command</em>'.
   * @see uk.co.saiman.eclipse.model.ui.HandledCell#getWbCommand()
   * @see #getHandledCell()
   * @generated
   */
  EAttribute getHandledCell_WbCommand();

  /**
   * Returns the meta object for the containment reference list '{@link uk.co.saiman.eclipse.model.ui.HandledCell#getParameters <em>Parameters</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Parameters</em>'.
   * @see uk.co.saiman.eclipse.model.ui.HandledCell#getParameters()
   * @see #getHandledCell()
   * @generated
   */
  EReference getHandledCell_Parameters();

  /**
   * Returns the meta object for class '{@link uk.co.saiman.eclipse.model.ui.EditableCell <em>Editable Cell</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Editable Cell</em>'.
   * @see uk.co.saiman.eclipse.model.ui.EditableCell
   * @generated
   */
  EClass getEditableCell();

  /**
   * Returns the meta object for the attribute '{@link uk.co.saiman.eclipse.model.ui.EditableCell#isEditing <em>Editing</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Editing</em>'.
   * @see uk.co.saiman.eclipse.model.ui.EditableCell#isEditing()
   * @see #getEditableCell()
   * @generated
   */
  EAttribute getEditableCell_Editing();

  /**
   * Returns the meta object for data type '{@link uk.co.saiman.data.format.MediaType <em>Media Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * A set of transfer formats is populated automatically to satisfy the
     * media types accepted by the cell. It can be modified
     * manually to customize serialization behavior.
     * <p>
     * Transfer formats are used to serialize and deserialize a cell's data in order
     * to facilitate, for example, drag-and-drop or copy-and-paste functionality.
     * <!-- end-model-doc -->
   * @return the meta object for data type '<em>Media Type</em>'.
   * @see uk.co.saiman.data.format.MediaType
   * @model instanceClass="uk.co.saiman.data.format.MediaType"
   * @generated
   */
  EDataType getMediaType();

  /**
   * Returns the meta object for data type '{@link uk.co.saiman.eclipse.ui.TransferFormat <em>Transfer Format</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Transfer Format</em>'.
   * @see uk.co.saiman.eclipse.ui.TransferFormat
   * @model instanceClass="uk.co.saiman.eclipse.ui.TransferFormat" serializeable="false" typeParameters="T"
   * @generated
   */
  EDataType getTransferFormat();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Factory getFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each operation of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals {
    /**
     * The meta object literal for the '{@link uk.co.saiman.eclipse.model.ui.impl.CellImpl <em>Cell</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see uk.co.saiman.eclipse.model.ui.impl.CellImpl
     * @see uk.co.saiman.eclipse.model.ui.impl.PackageImpl#getCell()
     * @generated
     */
    EClass CELL = eINSTANCE.getCell();

    /**
     * The meta object literal for the '<em><b>Media Types</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CELL__MEDIA_TYPES = eINSTANCE.getCell_MediaTypes();

    /**
     * The meta object literal for the '<em><b>Popup Menu</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CELL__POPUP_MENU = eINSTANCE.getCell_PopupMenu();

    /**
     * The meta object literal for the '<em><b>Transfer Formats</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CELL__TRANSFER_FORMATS = eINSTANCE.getCell_TransferFormats();

    /**
     * The meta object literal for the '<em><b>Context Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CELL__CONTEXT_VALUE = eINSTANCE.getCell_ContextValue();

    /**
     * The meta object literal for the '<em><b>Expanded</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CELL__EXPANDED = eINSTANCE.getCell_Expanded();

    /**
     * The meta object literal for the '<em><b>Nullable</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CELL__NULLABLE = eINSTANCE.getCell_Nullable();

    /**
     * The meta object literal for the '{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl <em>Tree</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see uk.co.saiman.eclipse.model.ui.impl.TreeImpl
     * @see uk.co.saiman.eclipse.model.ui.impl.PackageImpl#getTree()
     * @generated
     */
    EClass TREE = eINSTANCE.getTree();

    /**
     * The meta object literal for the '<em><b>Editable</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TREE__EDITABLE = eINSTANCE.getTree_Editable();

    /**
     * The meta object literal for the '{@link uk.co.saiman.eclipse.model.ui.impl.HandledCellImpl <em>Handled Cell</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see uk.co.saiman.eclipse.model.ui.impl.HandledCellImpl
     * @see uk.co.saiman.eclipse.model.ui.impl.PackageImpl#getHandledCell()
     * @generated
     */
    EClass HANDLED_CELL = eINSTANCE.getHandledCell();

    /**
     * The meta object literal for the '<em><b>Command</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference HANDLED_CELL__COMMAND = eINSTANCE.getHandledCell_Command();

    /**
     * The meta object literal for the '<em><b>Wb Command</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute HANDLED_CELL__WB_COMMAND = eINSTANCE.getHandledCell_WbCommand();

    /**
     * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference HANDLED_CELL__PARAMETERS = eINSTANCE.getHandledCell_Parameters();

    /**
     * The meta object literal for the '{@link uk.co.saiman.eclipse.model.ui.impl.EditableCellImpl <em>Editable Cell</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see uk.co.saiman.eclipse.model.ui.impl.EditableCellImpl
     * @see uk.co.saiman.eclipse.model.ui.impl.PackageImpl#getEditableCell()
     * @generated
     */
    EClass EDITABLE_CELL = eINSTANCE.getEditableCell();

    /**
     * The meta object literal for the '<em><b>Editing</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EDITABLE_CELL__EDITING = eINSTANCE.getEditableCell_Editing();

    /**
     * The meta object literal for the '<em>Media Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see uk.co.saiman.data.format.MediaType
     * @see uk.co.saiman.eclipse.model.ui.impl.PackageImpl#getMediaType()
     * @generated
     */
    EDataType MEDIA_TYPE = eINSTANCE.getMediaType();

    /**
     * The meta object literal for the '<em>Transfer Format</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see uk.co.saiman.eclipse.ui.TransferFormat
     * @see uk.co.saiman.eclipse.model.ui.impl.PackageImpl#getTransferFormat()
     * @generated
     */
    EDataType TRANSFER_FORMAT = eINSTANCE.getTransferFormat();

  }

} //Package
