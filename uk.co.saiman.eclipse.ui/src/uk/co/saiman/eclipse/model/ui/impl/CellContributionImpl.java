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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.contexts.IEclipseContext;

import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.MContribution;

import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.e4.ui.model.application.impl.StringToObjectMapImpl;
import org.eclipse.e4.ui.model.application.impl.StringToStringMapImpl;

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
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.CellContribution;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Cell Contribution</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellContributionImpl#getContext <em>Context</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellContributionImpl#getVariables <em>Variables</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellContributionImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellContributionImpl#getElementId <em>Element Id</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellContributionImpl#getPersistedState <em>Persisted State</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellContributionImpl#getTags <em>Tags</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellContributionImpl#getContributorURI <em>Contributor URI</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellContributionImpl#getTransientData <em>Transient Data</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellContributionImpl#getContributionURI <em>Contribution URI</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellContributionImpl#getObject <em>Object</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.CellContributionImpl#getParent <em>Parent</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CellContributionImpl extends org.eclipse.emf.ecore.impl.MinimalEObjectImpl.Container implements CellContribution {
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
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CellContributionImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return uk.co.saiman.eclipse.model.ui.Package.Literals.CELL_CONTRIBUTION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IEclipseContext getContext() {
    return context;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setContext(IEclipseContext newContext) {
    IEclipseContext oldContext = context;
    context = newContext;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTEXT, oldContext, context));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<String> getVariables() {
    if (variables == null) {
      variables = new EDataTypeUniqueEList<String>(String.class, this, uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__VARIABLES);
    }
    return variables;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map<String, String> getProperties() {
    if (properties == null) {
      properties = new EcoreEMap<String,String>(ApplicationPackageImpl.Literals.STRING_TO_STRING_MAP, StringToStringMapImpl.class, this, uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PROPERTIES);
    }
    return properties.map();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getElementId() {
    return elementId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setElementId(String newElementId) {
    String oldElementId = elementId;
    elementId = newElementId;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__ELEMENT_ID, oldElementId, elementId));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map<String, String> getPersistedState() {
    if (persistedState == null) {
      persistedState = new EcoreEMap<String,String>(ApplicationPackageImpl.Literals.STRING_TO_STRING_MAP, StringToStringMapImpl.class, this, uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PERSISTED_STATE);
    }
    return persistedState.map();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<String> getTags() {
    if (tags == null) {
      tags = new EDataTypeUniqueEList<String>(String.class, this, uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TAGS);
    }
    return tags;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getContributorURI() {
    return contributorURI;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setContributorURI(String newContributorURI) {
    String oldContributorURI = contributorURI;
    contributorURI = newContributorURI;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTOR_URI, oldContributorURI, contributorURI));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map<String, Object> getTransientData() {
    if (transientData == null) {
      transientData = new EcoreEMap<String,Object>(ApplicationPackageImpl.Literals.STRING_TO_OBJECT_MAP, StringToObjectMapImpl.class, this, uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TRANSIENT_DATA);
    }
    return transientData.map();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  public String getContributionURI() {
    return contributionURI;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  public void setContributionURI(String newContributionURI) {
    String oldContributionURI = contributionURI;
    contributionURI = newContributionURI;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTION_URI, oldContributionURI, contributionURI));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object getObject() {
    return object;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setObject(Object newObject) {
    Object oldObject = object;
    object = newObject;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__OBJECT, oldObject, object));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Cell getParent() {
    if (eContainerFeatureID() != uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PARENT) return null;
    return (Cell)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParent(Cell newParent, NotificationChain msgs) {
    msgs = eBasicSetContainer((InternalEObject)newParent, uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PARENT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setParent(Cell newParent) {
    if (newParent != eInternalContainer() || (eContainerFeatureID() != uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PARENT && newParent != null)) {
      if (EcoreUtil.isAncestor(this, (EObject)newParent))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newParent != null)
        msgs = ((InternalEObject)newParent).eInverseAdd(this, uk.co.saiman.eclipse.model.ui.Package.CELL__CONTRIBUTIONS, Cell.class, msgs);
      msgs = basicSetParent(newParent, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PARENT, newParent, newParent));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PARENT:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return basicSetParent((Cell)otherEnd, msgs);
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
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PROPERTIES:
        return ((InternalEList<?>)((EMap.InternalMapView<String, String>)getProperties()).eMap()).basicRemove(otherEnd, msgs);
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PERSISTED_STATE:
        return ((InternalEList<?>)((EMap.InternalMapView<String, String>)getPersistedState()).eMap()).basicRemove(otherEnd, msgs);
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TRANSIENT_DATA:
        return ((InternalEList<?>)((EMap.InternalMapView<String, Object>)getTransientData()).eMap()).basicRemove(otherEnd, msgs);
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PARENT:
        return basicSetParent(null, msgs);
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
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PARENT:
        return eInternalContainer().eInverseRemove(this, uk.co.saiman.eclipse.model.ui.Package.CELL__CONTRIBUTIONS, Cell.class, msgs);
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
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTEXT:
        return getContext();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__VARIABLES:
        return getVariables();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PROPERTIES:
        if (coreType) return ((EMap.InternalMapView<String, String>)getProperties()).eMap();
        else return getProperties();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__ELEMENT_ID:
        return getElementId();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PERSISTED_STATE:
        if (coreType) return ((EMap.InternalMapView<String, String>)getPersistedState()).eMap();
        else return getPersistedState();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TAGS:
        return getTags();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTOR_URI:
        return getContributorURI();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TRANSIENT_DATA:
        if (coreType) return ((EMap.InternalMapView<String, Object>)getTransientData()).eMap();
        else return getTransientData();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTION_URI:
        return getContributionURI();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__OBJECT:
        return getObject();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PARENT:
        return getParent();
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
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTEXT:
        setContext((IEclipseContext)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__VARIABLES:
        getVariables().clear();
        getVariables().addAll((Collection<? extends String>)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PROPERTIES:
        ((EStructuralFeature.Setting)((EMap.InternalMapView<String, String>)getProperties()).eMap()).set(newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__ELEMENT_ID:
        setElementId((String)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PERSISTED_STATE:
        ((EStructuralFeature.Setting)((EMap.InternalMapView<String, String>)getPersistedState()).eMap()).set(newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TAGS:
        getTags().clear();
        getTags().addAll((Collection<? extends String>)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTOR_URI:
        setContributorURI((String)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TRANSIENT_DATA:
        ((EStructuralFeature.Setting)((EMap.InternalMapView<String, Object>)getTransientData()).eMap()).set(newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTION_URI:
        setContributionURI((String)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__OBJECT:
        setObject(newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PARENT:
        setParent((Cell)newValue);
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
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTEXT:
        setContext(CONTEXT_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__VARIABLES:
        getVariables().clear();
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PROPERTIES:
        getProperties().clear();
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__ELEMENT_ID:
        setElementId(ELEMENT_ID_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PERSISTED_STATE:
        getPersistedState().clear();
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TAGS:
        getTags().clear();
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTOR_URI:
        setContributorURI(CONTRIBUTOR_URI_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TRANSIENT_DATA:
        getTransientData().clear();
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTION_URI:
        setContributionURI(CONTRIBUTION_URI_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__OBJECT:
        setObject(OBJECT_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PARENT:
        setParent((Cell)null);
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
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTEXT:
        return CONTEXT_EDEFAULT == null ? context != null : !CONTEXT_EDEFAULT.equals(context);
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__VARIABLES:
        return variables != null && !variables.isEmpty();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PROPERTIES:
        return properties != null && !properties.isEmpty();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__ELEMENT_ID:
        return ELEMENT_ID_EDEFAULT == null ? elementId != null : !ELEMENT_ID_EDEFAULT.equals(elementId);
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PERSISTED_STATE:
        return persistedState != null && !persistedState.isEmpty();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TAGS:
        return tags != null && !tags.isEmpty();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTOR_URI:
        return CONTRIBUTOR_URI_EDEFAULT == null ? contributorURI != null : !CONTRIBUTOR_URI_EDEFAULT.equals(contributorURI);
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TRANSIENT_DATA:
        return transientData != null && !transientData.isEmpty();
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTION_URI:
        return CONTRIBUTION_URI_EDEFAULT == null ? contributionURI != null : !CONTRIBUTION_URI_EDEFAULT.equals(contributionURI);
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__OBJECT:
        return OBJECT_EDEFAULT == null ? object != null : !OBJECT_EDEFAULT.equals(object);
      case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PARENT:
        return getParent() != null;
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
        case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__ELEMENT_ID: return ApplicationPackageImpl.APPLICATION_ELEMENT__ELEMENT_ID;
        case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PERSISTED_STATE: return ApplicationPackageImpl.APPLICATION_ELEMENT__PERSISTED_STATE;
        case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TAGS: return ApplicationPackageImpl.APPLICATION_ELEMENT__TAGS;
        case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTOR_URI: return ApplicationPackageImpl.APPLICATION_ELEMENT__CONTRIBUTOR_URI;
        case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TRANSIENT_DATA: return ApplicationPackageImpl.APPLICATION_ELEMENT__TRANSIENT_DATA;
        default: return -1;
      }
    }
    if (baseClass == MContribution.class) {
      switch (derivedFeatureID) {
        case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTION_URI: return ApplicationPackageImpl.CONTRIBUTION__CONTRIBUTION_URI;
        case uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__OBJECT: return ApplicationPackageImpl.CONTRIBUTION__OBJECT;
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
        case ApplicationPackageImpl.APPLICATION_ELEMENT__ELEMENT_ID: return uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__ELEMENT_ID;
        case ApplicationPackageImpl.APPLICATION_ELEMENT__PERSISTED_STATE: return uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__PERSISTED_STATE;
        case ApplicationPackageImpl.APPLICATION_ELEMENT__TAGS: return uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TAGS;
        case ApplicationPackageImpl.APPLICATION_ELEMENT__CONTRIBUTOR_URI: return uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTOR_URI;
        case ApplicationPackageImpl.APPLICATION_ELEMENT__TRANSIENT_DATA: return uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__TRANSIENT_DATA;
        default: return -1;
      }
    }
    if (baseClass == MContribution.class) {
      switch (baseFeatureID) {
        case ApplicationPackageImpl.CONTRIBUTION__CONTRIBUTION_URI: return uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__CONTRIBUTION_URI;
        case ApplicationPackageImpl.CONTRIBUTION__OBJECT: return uk.co.saiman.eclipse.model.ui.Package.CELL_CONTRIBUTION__OBJECT;
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
    result.append(')');
    return result.toString();
  }

} //CellContributionImpl
