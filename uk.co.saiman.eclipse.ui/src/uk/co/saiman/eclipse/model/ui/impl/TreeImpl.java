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

import java.lang.reflect.InvocationTargetException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.contexts.IEclipseContext;

import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.MContribution;

import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.e4.ui.model.application.impl.StringToObjectMapImpl;
import org.eclipse.e4.ui.model.application.impl.StringToStringMapImpl;

import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MExpression;
import org.eclipse.e4.ui.model.application.ui.MLocalizable;
import org.eclipse.e4.ui.model.application.ui.MUIElement;

import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;

import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.Tree;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Tree</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getContext <em>Context</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getVariables <em>Variables</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getElementId <em>Element Id</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getPersistedState <em>Persisted State</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getTags <em>Tags</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getContributorURI <em>Contributor URI</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getTransientData <em>Transient Data</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getContributionURI <em>Contribution URI</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getObject <em>Object</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getWidget <em>Widget</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getRenderer <em>Renderer</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#isToBeRendered <em>To Be Rendered</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#isOnTop <em>On Top</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#isVisible <em>Visible</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getContainerData <em>Container Data</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getCurSharedRef <em>Cur Shared Ref</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getVisibleWhen <em>Visible When</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getAccessibilityPhrase <em>Accessibility Phrase</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getLocalizedAccessibilityPhrase <em>Localized Accessibility Phrase</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#getSelectedElement <em>Selected Element</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.TreeImpl#isEditable <em>Editable</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TreeImpl extends org.eclipse.emf.ecore.impl.MinimalEObjectImpl.Container implements Tree {
  /**
   * The default value of the '{@link #getContext() <em>Context</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContext()
   * @generated
   * @ordered
   */
  protected static final IEclipseContext CONTEXT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getContext() <em>Context</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContext()
   * @generated
   * @ordered
   */
  protected IEclipseContext context = CONTEXT_EDEFAULT;

  /**
   * The cached value of the '{@link #getVariables() <em>Variables</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVariables()
   * @generated
   * @ordered
   */
  protected EList<String> variables;

  /**
   * The cached value of the '{@link #getProperties() <em>Properties</em>}' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getProperties()
   * @generated
   * @ordered
   */
  protected EMap<String, String> properties;

  /**
   * The default value of the '{@link #getElementId() <em>Element Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getElementId()
   * @generated
   * @ordered
   */
  protected static final String ELEMENT_ID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getElementId() <em>Element Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getElementId()
   * @generated
   * @ordered
   */
  protected String elementId = ELEMENT_ID_EDEFAULT;

  /**
   * The cached value of the '{@link #getPersistedState() <em>Persisted State</em>}' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPersistedState()
   * @generated
   * @ordered
   */
  protected EMap<String, String> persistedState;

  /**
   * The cached value of the '{@link #getTags() <em>Tags</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTags()
   * @generated
   * @ordered
   */
  protected EList<String> tags;

  /**
   * The default value of the '{@link #getContributorURI() <em>Contributor URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContributorURI()
   * @generated
   * @ordered
   */
  protected static final String CONTRIBUTOR_URI_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getContributorURI() <em>Contributor URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContributorURI()
   * @generated
   * @ordered
   */
  protected String contributorURI = CONTRIBUTOR_URI_EDEFAULT;

  /**
   * The cached value of the '{@link #getTransientData() <em>Transient Data</em>}' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTransientData()
   * @generated
   * @ordered
   */
  protected EMap<String, Object> transientData;

  /**
   * The default value of the '{@link #getContributionURI() <em>Contribution URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContributionURI()
   * @since 1.0
   * @generated
   * @ordered
   */
  protected static final String CONTRIBUTION_URI_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getContributionURI() <em>Contribution URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContributionURI()
   * @since 1.0
   * @generated
   * @ordered
   */
  protected String contributionURI = CONTRIBUTION_URI_EDEFAULT;

  /**
   * The default value of the '{@link #getObject() <em>Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getObject()
   * @generated
   * @ordered
   */
  protected static final Object OBJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getObject() <em>Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getObject()
   * @generated
   * @ordered
   */
  protected Object object = OBJECT_EDEFAULT;

  /**
   * The default value of the '{@link #getWidget() <em>Widget</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWidget()
   * @generated
   * @ordered
   */
  protected static final Object WIDGET_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getWidget() <em>Widget</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWidget()
   * @generated
   * @ordered
   */
  protected Object widget = WIDGET_EDEFAULT;

  /**
   * The default value of the '{@link #getRenderer() <em>Renderer</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRenderer()
   * @generated
   * @ordered
   */
  protected static final Object RENDERER_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getRenderer() <em>Renderer</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRenderer()
   * @generated
   * @ordered
   */
  protected Object renderer = RENDERER_EDEFAULT;

  /**
   * The default value of the '{@link #isToBeRendered() <em>To Be Rendered</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isToBeRendered()
   * @generated
   * @ordered
   */
  protected static final boolean TO_BE_RENDERED_EDEFAULT = true;

  /**
   * The cached value of the '{@link #isToBeRendered() <em>To Be Rendered</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isToBeRendered()
   * @generated
   * @ordered
   */
  protected boolean toBeRendered = TO_BE_RENDERED_EDEFAULT;

  /**
   * The default value of the '{@link #isOnTop() <em>On Top</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isOnTop()
   * @generated
   * @ordered
   */
  protected static final boolean ON_TOP_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isOnTop() <em>On Top</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isOnTop()
   * @generated
   * @ordered
   */
  protected boolean onTop = ON_TOP_EDEFAULT;

  /**
   * The default value of the '{@link #isVisible() <em>Visible</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isVisible()
   * @generated
   * @ordered
   */
  protected static final boolean VISIBLE_EDEFAULT = true;

  /**
   * The cached value of the '{@link #isVisible() <em>Visible</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isVisible()
   * @generated
   * @ordered
   */
  protected boolean visible = VISIBLE_EDEFAULT;

  /**
   * The default value of the '{@link #getContainerData() <em>Container Data</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContainerData()
   * @generated
   * @ordered
   */
  protected static final String CONTAINER_DATA_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getContainerData() <em>Container Data</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContainerData()
   * @generated
   * @ordered
   */
  protected String containerData = CONTAINER_DATA_EDEFAULT;

  /**
   * The cached value of the '{@link #getCurSharedRef() <em>Cur Shared Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCurSharedRef()
   * @generated
   * @ordered
   */
  protected MPlaceholder curSharedRef;

  /**
   * The cached value of the '{@link #getVisibleWhen() <em>Visible When</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVisibleWhen()
   * @generated
   * @ordered
   */
  protected MExpression visibleWhen;

  /**
   * The default value of the '{@link #getAccessibilityPhrase() <em>Accessibility Phrase</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAccessibilityPhrase()
   * @generated
   * @ordered
   */
  protected static final String ACCESSIBILITY_PHRASE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAccessibilityPhrase() <em>Accessibility Phrase</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAccessibilityPhrase()
   * @generated
   * @ordered
   */
  protected String accessibilityPhrase = ACCESSIBILITY_PHRASE_EDEFAULT;

  /**
   * The default value of the '{@link #getLocalizedAccessibilityPhrase() <em>Localized Accessibility Phrase</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLocalizedAccessibilityPhrase()
   * @generated
   * @ordered
   */
  protected static final String LOCALIZED_ACCESSIBILITY_PHRASE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getChildren()
   * @generated
   * @ordered
   */
  protected EList<Cell> children;

  /**
   * The cached value of the '{@link #getSelectedElement() <em>Selected Element</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSelectedElement()
   * @generated
   * @ordered
   */
  protected Cell selectedElement;

  /**
   * The default value of the '{@link #isEditable() <em>Editable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isEditable()
   * @generated
   * @ordered
   */
  protected static final boolean EDITABLE_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isEditable() <em>Editable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isEditable()
   * @generated
   * @ordered
   */
  protected boolean editable = EDITABLE_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TreeImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return uk.co.saiman.eclipse.model.ui.Package.Literals.TREE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public IEclipseContext getContext() {
    return context;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setContext(IEclipseContext newContext) {
    IEclipseContext oldContext = context;
    context = newContext;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__CONTEXT, oldContext, context));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public List<String> getVariables() {
    if (variables == null) {
      variables = new EDataTypeUniqueEList<String>(String.class, this, uk.co.saiman.eclipse.model.ui.Package.TREE__VARIABLES);
    }
    return variables;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Map<String, String> getProperties() {
    if (properties == null) {
      properties = new EcoreEMap<String,String>(ApplicationPackageImpl.Literals.STRING_TO_STRING_MAP, StringToStringMapImpl.class, this, uk.co.saiman.eclipse.model.ui.Package.TREE__PROPERTIES);
    }
    return properties.map();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getElementId() {
    return elementId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setElementId(String newElementId) {
    String oldElementId = elementId;
    elementId = newElementId;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__ELEMENT_ID, oldElementId, elementId));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Map<String, String> getPersistedState() {
    if (persistedState == null) {
      persistedState = new EcoreEMap<String,String>(ApplicationPackageImpl.Literals.STRING_TO_STRING_MAP, StringToStringMapImpl.class, this, uk.co.saiman.eclipse.model.ui.Package.TREE__PERSISTED_STATE);
    }
    return persistedState.map();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public List<String> getTags() {
    if (tags == null) {
      tags = new EDataTypeUniqueEList<String>(String.class, this, uk.co.saiman.eclipse.model.ui.Package.TREE__TAGS);
    }
    return tags;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getContributorURI() {
    return contributorURI;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setContributorURI(String newContributorURI) {
    String oldContributorURI = contributorURI;
    contributorURI = newContributorURI;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTOR_URI, oldContributorURI, contributorURI));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Map<String, Object> getTransientData() {
    if (transientData == null) {
      transientData = new EcoreEMap<String,Object>(ApplicationPackageImpl.Literals.STRING_TO_OBJECT_MAP, StringToObjectMapImpl.class, this, uk.co.saiman.eclipse.model.ui.Package.TREE__TRANSIENT_DATA);
    }
    return transientData.map();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  @Override
  public String getContributionURI() {
    return contributionURI;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  @Override
  public void setContributionURI(String newContributionURI) {
    String oldContributionURI = contributionURI;
    contributionURI = newContributionURI;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTION_URI, oldContributionURI, contributionURI));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getObject() {
    return object;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setObject(Object newObject) {
    Object oldObject = object;
    object = newObject;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__OBJECT, oldObject, object));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getWidget() {
    return widget;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setWidget(Object newWidget) {
    Object oldWidget = widget;
    widget = newWidget;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__WIDGET, oldWidget, widget));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getRenderer() {
    return renderer;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setRenderer(Object newRenderer) {
    Object oldRenderer = renderer;
    renderer = newRenderer;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__RENDERER, oldRenderer, renderer));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isToBeRendered() {
    return toBeRendered;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setToBeRendered(boolean newToBeRendered) {
    boolean oldToBeRendered = toBeRendered;
    toBeRendered = newToBeRendered;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__TO_BE_RENDERED, oldToBeRendered, toBeRendered));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isOnTop() {
    return onTop;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setOnTop(boolean newOnTop) {
    boolean oldOnTop = onTop;
    onTop = newOnTop;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__ON_TOP, oldOnTop, onTop));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isVisible() {
    return visible;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setVisible(boolean newVisible) {
    boolean oldVisible = visible;
    visible = newVisible;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE, oldVisible, visible));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public MElementContainer<MUIElement> getParent() {
    if (eContainerFeatureID() != uk.co.saiman.eclipse.model.ui.Package.TREE__PARENT) return null;
    return (MElementContainer<MUIElement>)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParent(MElementContainer<MUIElement> newParent, NotificationChain msgs) {
    msgs = eBasicSetContainer((InternalEObject)newParent, uk.co.saiman.eclipse.model.ui.Package.TREE__PARENT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setParent(MElementContainer<MUIElement> newParent) {
    if (newParent != eInternalContainer() || (eContainerFeatureID() != uk.co.saiman.eclipse.model.ui.Package.TREE__PARENT && newParent != null)) {
      if (EcoreUtil.isAncestor(this, (EObject)newParent))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newParent != null)
        msgs = ((InternalEObject)newParent).eInverseAdd(this, UiPackageImpl.ELEMENT_CONTAINER__CHILDREN, MElementContainer.class, msgs);
      msgs = basicSetParent(newParent, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__PARENT, newParent, newParent));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getContainerData() {
    return containerData;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setContainerData(String newContainerData) {
    String oldContainerData = containerData;
    containerData = newContainerData;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__CONTAINER_DATA, oldContainerData, containerData));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MPlaceholder getCurSharedRef() {
    if (curSharedRef != null && ((EObject)curSharedRef).eIsProxy()) {
      InternalEObject oldCurSharedRef = (InternalEObject)curSharedRef;
      curSharedRef = (MPlaceholder)eResolveProxy(oldCurSharedRef);
      if (curSharedRef != oldCurSharedRef) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, uk.co.saiman.eclipse.model.ui.Package.TREE__CUR_SHARED_REF, oldCurSharedRef, curSharedRef));
      }
    }
    return curSharedRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MPlaceholder basicGetCurSharedRef() {
    return curSharedRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setCurSharedRef(MPlaceholder newCurSharedRef) {
    MPlaceholder oldCurSharedRef = curSharedRef;
    curSharedRef = newCurSharedRef;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__CUR_SHARED_REF, oldCurSharedRef, curSharedRef));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MExpression getVisibleWhen() {
    return visibleWhen;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetVisibleWhen(MExpression newVisibleWhen, NotificationChain msgs) {
    MExpression oldVisibleWhen = visibleWhen;
    visibleWhen = newVisibleWhen;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE_WHEN, oldVisibleWhen, newVisibleWhen);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setVisibleWhen(MExpression newVisibleWhen) {
    if (newVisibleWhen != visibleWhen) {
      NotificationChain msgs = null;
      if (visibleWhen != null)
        msgs = ((InternalEObject)visibleWhen).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE_WHEN, null, msgs);
      if (newVisibleWhen != null)
        msgs = ((InternalEObject)newVisibleWhen).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE_WHEN, null, msgs);
      msgs = basicSetVisibleWhen(newVisibleWhen, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE_WHEN, newVisibleWhen, newVisibleWhen));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAccessibilityPhrase() {
    return accessibilityPhrase;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAccessibilityPhrase(String newAccessibilityPhrase) {
    String oldAccessibilityPhrase = accessibilityPhrase;
    accessibilityPhrase = newAccessibilityPhrase;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__ACCESSIBILITY_PHRASE, oldAccessibilityPhrase, accessibilityPhrase));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * \@generated
   */
  @Override
  public String getLocalizedAccessibilityPhrase() {
    // TODO: implement this method to return the 'Localized Accessibility Phrase' attribute
    // Ensure that you remove @generated or mark it @generated NOT
    //throw new UnsupportedOperationException();

    return getAccessibilityPhrase();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public List<Cell> getChildren() {
    if (children == null) {
      children = new EObjectContainmentWithInverseEList<Cell>(Cell.class, this, uk.co.saiman.eclipse.model.ui.Package.TREE__CHILDREN, UiPackageImpl.UI_ELEMENT__PARENT) { private static final long serialVersionUID = 1L; @Override public Class<?> getInverseFeatureClass() { return MUIElement.class; } };
    }
    return children;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Cell getSelectedElement() {
    if (selectedElement != null && ((EObject)selectedElement).eIsProxy()) {
      InternalEObject oldSelectedElement = (InternalEObject)selectedElement;
      selectedElement = (Cell)eResolveProxy(oldSelectedElement);
      if (selectedElement != oldSelectedElement) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, uk.co.saiman.eclipse.model.ui.Package.TREE__SELECTED_ELEMENT, oldSelectedElement, selectedElement));
      }
    }
    return selectedElement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Cell basicGetSelectedElement() {
    return selectedElement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSelectedElement(Cell newSelectedElement) {
    Cell oldSelectedElement = selectedElement;
    selectedElement = newSelectedElement;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__SELECTED_ELEMENT, oldSelectedElement, selectedElement));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isEditable() {
    return editable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEditable(boolean newEditable) {
    boolean oldEditable = editable;
    editable = newEditable;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.TREE__EDITABLE, oldEditable, editable));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * \@generated
   */
  @Override
  public void updateLocalization() {
    // TODO: implement this method
    // Ensure that you remove @generated or mark it @generated NOT
    //throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PARENT:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return basicSetParent((MElementContainer<MUIElement>)otherEnd, msgs);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CHILDREN:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getChildren()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PROPERTIES:
        return ((InternalEList<?>)((EMap.InternalMapView<String, String>)getProperties()).eMap()).basicRemove(otherEnd, msgs);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PERSISTED_STATE:
        return ((InternalEList<?>)((EMap.InternalMapView<String, String>)getPersistedState()).eMap()).basicRemove(otherEnd, msgs);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__TRANSIENT_DATA:
        return ((InternalEList<?>)((EMap.InternalMapView<String, Object>)getTransientData()).eMap()).basicRemove(otherEnd, msgs);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PARENT:
        return basicSetParent(null, msgs);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE_WHEN:
        return basicSetVisibleWhen(null, msgs);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CHILDREN:
        return ((InternalEList<?>)getChildren()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
    switch (eContainerFeatureID()) {
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PARENT:
        return eInternalContainer().eInverseRemove(this, UiPackageImpl.ELEMENT_CONTAINER__CHILDREN, MElementContainer.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTEXT:
        return getContext();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__VARIABLES:
        return getVariables();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PROPERTIES:
        if (coreType) return ((EMap.InternalMapView<String, String>)getProperties()).eMap();
        else return getProperties();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__ELEMENT_ID:
        return getElementId();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PERSISTED_STATE:
        if (coreType) return ((EMap.InternalMapView<String, String>)getPersistedState()).eMap();
        else return getPersistedState();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__TAGS:
        return getTags();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTOR_URI:
        return getContributorURI();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__TRANSIENT_DATA:
        if (coreType) return ((EMap.InternalMapView<String, Object>)getTransientData()).eMap();
        else return getTransientData();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTION_URI:
        return getContributionURI();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__OBJECT:
        return getObject();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__WIDGET:
        return getWidget();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__RENDERER:
        return getRenderer();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__TO_BE_RENDERED:
        return isToBeRendered();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__ON_TOP:
        return isOnTop();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE:
        return isVisible();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PARENT:
        return getParent();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTAINER_DATA:
        return getContainerData();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CUR_SHARED_REF:
        if (resolve) return getCurSharedRef();
        return basicGetCurSharedRef();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE_WHEN:
        return getVisibleWhen();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__ACCESSIBILITY_PHRASE:
        return getAccessibilityPhrase();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__LOCALIZED_ACCESSIBILITY_PHRASE:
        return getLocalizedAccessibilityPhrase();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CHILDREN:
        return getChildren();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__SELECTED_ELEMENT:
        if (resolve) return getSelectedElement();
        return basicGetSelectedElement();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__EDITABLE:
        return isEditable();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue) {
    switch (featureID) {
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTEXT:
        setContext((IEclipseContext)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__VARIABLES:
        getVariables().clear();
        getVariables().addAll((Collection<? extends String>)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PROPERTIES:
        ((EStructuralFeature.Setting)((EMap.InternalMapView<String, String>)getProperties()).eMap()).set(newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__ELEMENT_ID:
        setElementId((String)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PERSISTED_STATE:
        ((EStructuralFeature.Setting)((EMap.InternalMapView<String, String>)getPersistedState()).eMap()).set(newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__TAGS:
        getTags().clear();
        getTags().addAll((Collection<? extends String>)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTOR_URI:
        setContributorURI((String)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__TRANSIENT_DATA:
        ((EStructuralFeature.Setting)((EMap.InternalMapView<String, Object>)getTransientData()).eMap()).set(newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTION_URI:
        setContributionURI((String)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__OBJECT:
        setObject(newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__WIDGET:
        setWidget(newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__RENDERER:
        setRenderer(newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__TO_BE_RENDERED:
        setToBeRendered((Boolean)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__ON_TOP:
        setOnTop((Boolean)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE:
        setVisible((Boolean)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PARENT:
        setParent((MElementContainer<MUIElement>)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTAINER_DATA:
        setContainerData((String)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CUR_SHARED_REF:
        setCurSharedRef((MPlaceholder)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE_WHEN:
        setVisibleWhen((MExpression)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__ACCESSIBILITY_PHRASE:
        setAccessibilityPhrase((String)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CHILDREN:
        getChildren().clear();
        getChildren().addAll((Collection<? extends Cell>)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__SELECTED_ELEMENT:
        setSelectedElement((Cell)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__EDITABLE:
        setEditable((Boolean)newValue);
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
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTEXT:
        setContext(CONTEXT_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__VARIABLES:
        getVariables().clear();
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PROPERTIES:
        getProperties().clear();
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__ELEMENT_ID:
        setElementId(ELEMENT_ID_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PERSISTED_STATE:
        getPersistedState().clear();
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__TAGS:
        getTags().clear();
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTOR_URI:
        setContributorURI(CONTRIBUTOR_URI_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__TRANSIENT_DATA:
        getTransientData().clear();
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTION_URI:
        setContributionURI(CONTRIBUTION_URI_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__OBJECT:
        setObject(OBJECT_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__WIDGET:
        setWidget(WIDGET_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__RENDERER:
        setRenderer(RENDERER_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__TO_BE_RENDERED:
        setToBeRendered(TO_BE_RENDERED_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__ON_TOP:
        setOnTop(ON_TOP_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE:
        setVisible(VISIBLE_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PARENT:
        setParent((MElementContainer<MUIElement>)null);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTAINER_DATA:
        setContainerData(CONTAINER_DATA_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CUR_SHARED_REF:
        setCurSharedRef((MPlaceholder)null);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE_WHEN:
        setVisibleWhen((MExpression)null);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__ACCESSIBILITY_PHRASE:
        setAccessibilityPhrase(ACCESSIBILITY_PHRASE_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CHILDREN:
        getChildren().clear();
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__SELECTED_ELEMENT:
        setSelectedElement((Cell)null);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__EDITABLE:
        setEditable(EDITABLE_EDEFAULT);
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
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTEXT:
        return CONTEXT_EDEFAULT == null ? context != null : !CONTEXT_EDEFAULT.equals(context);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__VARIABLES:
        return variables != null && !variables.isEmpty();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PROPERTIES:
        return properties != null && !properties.isEmpty();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__ELEMENT_ID:
        return ELEMENT_ID_EDEFAULT == null ? elementId != null : !ELEMENT_ID_EDEFAULT.equals(elementId);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PERSISTED_STATE:
        return persistedState != null && !persistedState.isEmpty();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__TAGS:
        return tags != null && !tags.isEmpty();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTOR_URI:
        return CONTRIBUTOR_URI_EDEFAULT == null ? contributorURI != null : !CONTRIBUTOR_URI_EDEFAULT.equals(contributorURI);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__TRANSIENT_DATA:
        return transientData != null && !transientData.isEmpty();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTION_URI:
        return CONTRIBUTION_URI_EDEFAULT == null ? contributionURI != null : !CONTRIBUTION_URI_EDEFAULT.equals(contributionURI);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__OBJECT:
        return OBJECT_EDEFAULT == null ? object != null : !OBJECT_EDEFAULT.equals(object);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__WIDGET:
        return WIDGET_EDEFAULT == null ? widget != null : !WIDGET_EDEFAULT.equals(widget);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__RENDERER:
        return RENDERER_EDEFAULT == null ? renderer != null : !RENDERER_EDEFAULT.equals(renderer);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__TO_BE_RENDERED:
        return toBeRendered != TO_BE_RENDERED_EDEFAULT;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__ON_TOP:
        return onTop != ON_TOP_EDEFAULT;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE:
        return visible != VISIBLE_EDEFAULT;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__PARENT:
        return getParent() != null;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTAINER_DATA:
        return CONTAINER_DATA_EDEFAULT == null ? containerData != null : !CONTAINER_DATA_EDEFAULT.equals(containerData);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CUR_SHARED_REF:
        return curSharedRef != null;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE_WHEN:
        return visibleWhen != null;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__ACCESSIBILITY_PHRASE:
        return ACCESSIBILITY_PHRASE_EDEFAULT == null ? accessibilityPhrase != null : !ACCESSIBILITY_PHRASE_EDEFAULT.equals(accessibilityPhrase);
      case uk.co.saiman.eclipse.model.ui.Package.TREE__LOCALIZED_ACCESSIBILITY_PHRASE:
        return LOCALIZED_ACCESSIBILITY_PHRASE_EDEFAULT == null ? getLocalizedAccessibilityPhrase() != null : !LOCALIZED_ACCESSIBILITY_PHRASE_EDEFAULT.equals(getLocalizedAccessibilityPhrase());
      case uk.co.saiman.eclipse.model.ui.Package.TREE__CHILDREN:
        return children != null && !children.isEmpty();
      case uk.co.saiman.eclipse.model.ui.Package.TREE__SELECTED_ELEMENT:
        return selectedElement != null;
      case uk.co.saiman.eclipse.model.ui.Package.TREE__EDITABLE:
        return editable != EDITABLE_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
    if (baseClass == MApplicationElement.class) {
      switch (derivedFeatureID) {
        case uk.co.saiman.eclipse.model.ui.Package.TREE__ELEMENT_ID: return ApplicationPackageImpl.APPLICATION_ELEMENT__ELEMENT_ID;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__PERSISTED_STATE: return ApplicationPackageImpl.APPLICATION_ELEMENT__PERSISTED_STATE;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__TAGS: return ApplicationPackageImpl.APPLICATION_ELEMENT__TAGS;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTOR_URI: return ApplicationPackageImpl.APPLICATION_ELEMENT__CONTRIBUTOR_URI;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__TRANSIENT_DATA: return ApplicationPackageImpl.APPLICATION_ELEMENT__TRANSIENT_DATA;
        default: return -1;
      }
    }
    if (baseClass == MContribution.class) {
      switch (derivedFeatureID) {
        case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTION_URI: return ApplicationPackageImpl.CONTRIBUTION__CONTRIBUTION_URI;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__OBJECT: return ApplicationPackageImpl.CONTRIBUTION__OBJECT;
        default: return -1;
      }
    }
    if (baseClass == MLocalizable.class) {
      switch (derivedFeatureID) {
        default: return -1;
      }
    }
    if (baseClass == MUIElement.class) {
      switch (derivedFeatureID) {
        case uk.co.saiman.eclipse.model.ui.Package.TREE__WIDGET: return UiPackageImpl.UI_ELEMENT__WIDGET;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__RENDERER: return UiPackageImpl.UI_ELEMENT__RENDERER;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__TO_BE_RENDERED: return UiPackageImpl.UI_ELEMENT__TO_BE_RENDERED;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__ON_TOP: return UiPackageImpl.UI_ELEMENT__ON_TOP;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE: return UiPackageImpl.UI_ELEMENT__VISIBLE;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__PARENT: return UiPackageImpl.UI_ELEMENT__PARENT;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__CONTAINER_DATA: return UiPackageImpl.UI_ELEMENT__CONTAINER_DATA;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__CUR_SHARED_REF: return UiPackageImpl.UI_ELEMENT__CUR_SHARED_REF;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE_WHEN: return UiPackageImpl.UI_ELEMENT__VISIBLE_WHEN;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__ACCESSIBILITY_PHRASE: return UiPackageImpl.UI_ELEMENT__ACCESSIBILITY_PHRASE;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__LOCALIZED_ACCESSIBILITY_PHRASE: return UiPackageImpl.UI_ELEMENT__LOCALIZED_ACCESSIBILITY_PHRASE;
        default: return -1;
      }
    }
    if (baseClass == MElementContainer.class) {
      switch (derivedFeatureID) {
        case uk.co.saiman.eclipse.model.ui.Package.TREE__CHILDREN: return UiPackageImpl.ELEMENT_CONTAINER__CHILDREN;
        case uk.co.saiman.eclipse.model.ui.Package.TREE__SELECTED_ELEMENT: return UiPackageImpl.ELEMENT_CONTAINER__SELECTED_ELEMENT;
        default: return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
    if (baseClass == MApplicationElement.class) {
      switch (baseFeatureID) {
        case ApplicationPackageImpl.APPLICATION_ELEMENT__ELEMENT_ID: return uk.co.saiman.eclipse.model.ui.Package.TREE__ELEMENT_ID;
        case ApplicationPackageImpl.APPLICATION_ELEMENT__PERSISTED_STATE: return uk.co.saiman.eclipse.model.ui.Package.TREE__PERSISTED_STATE;
        case ApplicationPackageImpl.APPLICATION_ELEMENT__TAGS: return uk.co.saiman.eclipse.model.ui.Package.TREE__TAGS;
        case ApplicationPackageImpl.APPLICATION_ELEMENT__CONTRIBUTOR_URI: return uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTOR_URI;
        case ApplicationPackageImpl.APPLICATION_ELEMENT__TRANSIENT_DATA: return uk.co.saiman.eclipse.model.ui.Package.TREE__TRANSIENT_DATA;
        default: return -1;
      }
    }
    if (baseClass == MContribution.class) {
      switch (baseFeatureID) {
        case ApplicationPackageImpl.CONTRIBUTION__CONTRIBUTION_URI: return uk.co.saiman.eclipse.model.ui.Package.TREE__CONTRIBUTION_URI;
        case ApplicationPackageImpl.CONTRIBUTION__OBJECT: return uk.co.saiman.eclipse.model.ui.Package.TREE__OBJECT;
        default: return -1;
      }
    }
    if (baseClass == MLocalizable.class) {
      switch (baseFeatureID) {
        default: return -1;
      }
    }
    if (baseClass == MUIElement.class) {
      switch (baseFeatureID) {
        case UiPackageImpl.UI_ELEMENT__WIDGET: return uk.co.saiman.eclipse.model.ui.Package.TREE__WIDGET;
        case UiPackageImpl.UI_ELEMENT__RENDERER: return uk.co.saiman.eclipse.model.ui.Package.TREE__RENDERER;
        case UiPackageImpl.UI_ELEMENT__TO_BE_RENDERED: return uk.co.saiman.eclipse.model.ui.Package.TREE__TO_BE_RENDERED;
        case UiPackageImpl.UI_ELEMENT__ON_TOP: return uk.co.saiman.eclipse.model.ui.Package.TREE__ON_TOP;
        case UiPackageImpl.UI_ELEMENT__VISIBLE: return uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE;
        case UiPackageImpl.UI_ELEMENT__PARENT: return uk.co.saiman.eclipse.model.ui.Package.TREE__PARENT;
        case UiPackageImpl.UI_ELEMENT__CONTAINER_DATA: return uk.co.saiman.eclipse.model.ui.Package.TREE__CONTAINER_DATA;
        case UiPackageImpl.UI_ELEMENT__CUR_SHARED_REF: return uk.co.saiman.eclipse.model.ui.Package.TREE__CUR_SHARED_REF;
        case UiPackageImpl.UI_ELEMENT__VISIBLE_WHEN: return uk.co.saiman.eclipse.model.ui.Package.TREE__VISIBLE_WHEN;
        case UiPackageImpl.UI_ELEMENT__ACCESSIBILITY_PHRASE: return uk.co.saiman.eclipse.model.ui.Package.TREE__ACCESSIBILITY_PHRASE;
        case UiPackageImpl.UI_ELEMENT__LOCALIZED_ACCESSIBILITY_PHRASE: return uk.co.saiman.eclipse.model.ui.Package.TREE__LOCALIZED_ACCESSIBILITY_PHRASE;
        default: return -1;
      }
    }
    if (baseClass == MElementContainer.class) {
      switch (baseFeatureID) {
        case UiPackageImpl.ELEMENT_CONTAINER__CHILDREN: return uk.co.saiman.eclipse.model.ui.Package.TREE__CHILDREN;
        case UiPackageImpl.ELEMENT_CONTAINER__SELECTED_ELEMENT: return uk.co.saiman.eclipse.model.ui.Package.TREE__SELECTED_ELEMENT;
        default: return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedOperationID(int baseOperationID, Class<?> baseClass) {
    if (baseClass == MApplicationElement.class) {
      switch (baseOperationID) {
        default: return -1;
      }
    }
    if (baseClass == MContribution.class) {
      switch (baseOperationID) {
        default: return -1;
      }
    }
    if (baseClass == MLocalizable.class) {
      switch (baseOperationID) {
        case UiPackageImpl.LOCALIZABLE___UPDATE_LOCALIZATION: return uk.co.saiman.eclipse.model.ui.Package.TREE___UPDATE_LOCALIZATION;
        default: return -1;
      }
    }
    if (baseClass == MUIElement.class) {
      switch (baseOperationID) {
        default: return -1;
      }
    }
    if (baseClass == MElementContainer.class) {
      switch (baseOperationID) {
        default: return -1;
      }
    }
    return super.eDerivedOperationID(baseOperationID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
    switch (operationID) {
      case uk.co.saiman.eclipse.model.ui.Package.TREE___UPDATE_LOCALIZATION:
        updateLocalization();
        return null;
    }
    return super.eInvoke(operationID, arguments);
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
    result.append(" (context: ");
    result.append(context);
    result.append(", variables: ");
    result.append(variables);
    result.append(", elementId: ");
    result.append(elementId);
    result.append(", tags: ");
    result.append(tags);
    result.append(", contributorURI: ");
    result.append(contributorURI);
    result.append(", contributionURI: ");
    result.append(contributionURI);
    result.append(", object: ");
    result.append(object);
    result.append(", widget: ");
    result.append(widget);
    result.append(", renderer: ");
    result.append(renderer);
    result.append(", toBeRendered: ");
    result.append(toBeRendered);
    result.append(", onTop: ");
    result.append(onTop);
    result.append(", visible: ");
    result.append(visible);
    result.append(", containerData: ");
    result.append(containerData);
    result.append(", accessibilityPhrase: ");
    result.append(accessibilityPhrase);
    result.append(", editable: ");
    result.append(editable);
    result.append(')');
    return result.toString();
  }

} //TreeImpl
