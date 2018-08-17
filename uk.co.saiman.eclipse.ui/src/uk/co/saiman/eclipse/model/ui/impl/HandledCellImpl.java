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
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import uk.co.saiman.eclipse.model.ui.HandledCell;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Handled Cell</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.HandledCellImpl#isEnabled <em>Enabled</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.HandledCellImpl#isSelected <em>Selected</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.HandledCellImpl#getType <em>Type</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.HandledCellImpl#getCommand <em>Command</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.HandledCellImpl#getWbCommand <em>Wb Command</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.HandledCellImpl#getParameters <em>Parameters</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HandledCellImpl extends CellImpl implements HandledCell {
  /**
   * The default value of the '{@link #isEnabled() <em>Enabled</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isEnabled()
   * @generated
   * @ordered
   */
  protected static final boolean ENABLED_EDEFAULT = true;

  /**
   * The cached value of the '{@link #isEnabled() <em>Enabled</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isEnabled()
   * @generated
   * @ordered
   */
  protected boolean enabled = ENABLED_EDEFAULT;

  /**
   * The default value of the '{@link #isSelected() <em>Selected</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSelected()
   * @generated
   * @ordered
   */
  protected static final boolean SELECTED_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isSelected() <em>Selected</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSelected()
   * @generated
   * @ordered
   */
  protected boolean selected = SELECTED_EDEFAULT;

  /**
   * The default value of the '{@link #getType() <em>Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getType()
   * @generated
   * @ordered
   */
  protected static final ItemType TYPE_EDEFAULT = ItemType.PUSH;

  /**
   * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getType()
   * @generated
   * @ordered
   */
  protected ItemType type = TYPE_EDEFAULT;

  /**
   * The cached value of the '{@link #getCommand() <em>Command</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCommand()
   * @generated
   * @ordered
   */
  protected MCommand command;

  /**
   * The default value of the '{@link #getWbCommand() <em>Wb Command</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWbCommand()
   * @generated
   * @ordered
   */
  protected static final ParameterizedCommand WB_COMMAND_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getWbCommand() <em>Wb Command</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWbCommand()
   * @generated
   * @ordered
   */
  protected ParameterizedCommand wbCommand = WB_COMMAND_EDEFAULT;

  /**
   * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getParameters()
   * @generated
   * @ordered
   */
  protected EList<MParameter> parameters;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected HandledCellImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return uk.co.saiman.eclipse.model.ui.Package.Literals.HANDLED_CELL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEnabled(boolean newEnabled) {
    boolean oldEnabled = enabled;
    enabled = newEnabled;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__ENABLED, oldEnabled, enabled));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSelected(boolean newSelected) {
    boolean oldSelected = selected;
    selected = newSelected;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__SELECTED, oldSelected, selected));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ItemType getType() {
    return type;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setType(ItemType newType) {
    ItemType oldType = type;
    type = newType == null ? TYPE_EDEFAULT : newType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__TYPE, oldType, type));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MCommand getCommand() {
    if (command != null && ((EObject)command).eIsProxy()) {
      InternalEObject oldCommand = (InternalEObject)command;
      command = (MCommand)eResolveProxy(oldCommand);
      if (command != oldCommand) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__COMMAND, oldCommand, command));
      }
    }
    return command;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MCommand basicGetCommand() {
    return command;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setCommand(MCommand newCommand) {
    MCommand oldCommand = command;
    command = newCommand;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__COMMAND, oldCommand, command));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ParameterizedCommand getWbCommand() {
    return wbCommand;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setWbCommand(ParameterizedCommand newWbCommand) {
    ParameterizedCommand oldWbCommand = wbCommand;
    wbCommand = newWbCommand;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__WB_COMMAND, oldWbCommand, wbCommand));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<MParameter> getParameters() {
    if (parameters == null) {
      parameters = new EObjectContainmentEList<MParameter>(MParameter.class, this, uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__PARAMETERS);
    }
    return parameters;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__PARAMETERS:
        return ((InternalEList<?>)getParameters()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__ENABLED:
        return isEnabled();
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__SELECTED:
        return isSelected();
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__TYPE:
        return getType();
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__COMMAND:
        if (resolve) return getCommand();
        return basicGetCommand();
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__WB_COMMAND:
        return getWbCommand();
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__PARAMETERS:
        return getParameters();
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
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__ENABLED:
        setEnabled((Boolean)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__SELECTED:
        setSelected((Boolean)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__TYPE:
        setType((ItemType)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__COMMAND:
        setCommand((MCommand)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__WB_COMMAND:
        setWbCommand((ParameterizedCommand)newValue);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__PARAMETERS:
        getParameters().clear();
        getParameters().addAll((Collection<? extends MParameter>)newValue);
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
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__ENABLED:
        setEnabled(ENABLED_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__SELECTED:
        setSelected(SELECTED_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__TYPE:
        setType(TYPE_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__COMMAND:
        setCommand((MCommand)null);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__WB_COMMAND:
        setWbCommand(WB_COMMAND_EDEFAULT);
        return;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__PARAMETERS:
        getParameters().clear();
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
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__ENABLED:
        return enabled != ENABLED_EDEFAULT;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__SELECTED:
        return selected != SELECTED_EDEFAULT;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__TYPE:
        return type != TYPE_EDEFAULT;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__COMMAND:
        return command != null;
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__WB_COMMAND:
        return WB_COMMAND_EDEFAULT == null ? wbCommand != null : !WB_COMMAND_EDEFAULT.equals(wbCommand);
      case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__PARAMETERS:
        return parameters != null && !parameters.isEmpty();
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
    if (baseClass == MItem.class) {
      switch (derivedFeatureID) {
        case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__ENABLED: return MenuPackageImpl.ITEM__ENABLED;
        case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__SELECTED: return MenuPackageImpl.ITEM__SELECTED;
        case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__TYPE: return MenuPackageImpl.ITEM__TYPE;
        default: return -1;
      }
    }
    if (baseClass == MHandledItem.class) {
      switch (derivedFeatureID) {
        case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__COMMAND: return MenuPackageImpl.HANDLED_ITEM__COMMAND;
        case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__WB_COMMAND: return MenuPackageImpl.HANDLED_ITEM__WB_COMMAND;
        case uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__PARAMETERS: return MenuPackageImpl.HANDLED_ITEM__PARAMETERS;
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
    if (baseClass == MItem.class) {
      switch (baseFeatureID) {
        case MenuPackageImpl.ITEM__ENABLED: return uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__ENABLED;
        case MenuPackageImpl.ITEM__SELECTED: return uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__SELECTED;
        case MenuPackageImpl.ITEM__TYPE: return uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__TYPE;
        default: return -1;
      }
    }
    if (baseClass == MHandledItem.class) {
      switch (baseFeatureID) {
        case MenuPackageImpl.HANDLED_ITEM__COMMAND: return uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__COMMAND;
        case MenuPackageImpl.HANDLED_ITEM__WB_COMMAND: return uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__WB_COMMAND;
        case MenuPackageImpl.HANDLED_ITEM__PARAMETERS: return uk.co.saiman.eclipse.model.ui.Package.HANDLED_CELL__PARAMETERS;
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
    result.append(" (enabled: ");
    result.append(enabled);
    result.append(", selected: ");
    result.append(selected);
    result.append(", type: ");
    result.append(type);
    result.append(", wbCommand: ");
    result.append(wbCommand);
    result.append(')');
    return result.toString();
  }

} //HandledCellImpl
