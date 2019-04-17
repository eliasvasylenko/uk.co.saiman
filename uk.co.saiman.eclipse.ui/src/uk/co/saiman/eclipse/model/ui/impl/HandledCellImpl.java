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

import java.util.Collection;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
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
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.HandledCellImpl#getCommand <em>Command</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.HandledCellImpl#getWbCommand <em>Wb Command</em>}</li>
 *   <li>{@link uk.co.saiman.eclipse.model.ui.impl.HandledCellImpl#getParameters <em>Parameters</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HandledCellImpl extends CellImpl implements HandledCell {
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
  public EList<MParameter> getParameters() {
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
  public String toString() {
    if (eIsProxy()) return super.toString();

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (wbCommand: ");
    result.append(wbCommand);
    result.append(')');
    return result.toString();
  }

} //HandledCellImpl
