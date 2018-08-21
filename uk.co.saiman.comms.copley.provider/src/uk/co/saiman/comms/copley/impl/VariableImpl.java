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
 * This file is part of uk.co.saiman.comms.copley.provider.
 *
 * uk.co.saiman.comms.copley.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.impl;

import static uk.co.saiman.comms.copley.CopleyOperationID.COPY_VARIABLE;
import static uk.co.saiman.comms.copley.CopleyOperationID.GET_VARIABLE;
import static uk.co.saiman.comms.copley.CopleyOperationID.SET_VARIABLE;

import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.Variable;
import uk.co.saiman.comms.copley.VariableBank;
import uk.co.saiman.comms.copley.VariableIdentifier;

class VariableImpl<U> implements Variable<U> {
  private final CopleyNodeImpl controller;
  private final CopleyVariableID id;
  private final Class<U> variableClass;
  private final int axis;
  private final VariableBank bank;

  public VariableImpl(
      CopleyNodeImpl controller,
      CopleyVariableID id,
      Class<U> variableClass,
      int axis,
      VariableBank bank) {
    this.controller = controller;
    this.id = id;
    this.variableClass = variableClass;
    this.axis = axis;
    this.bank = bank;
  }

  public CopleyNodeImpl getController() {
    return controller;
  }

  @Override
  public CopleyVariableID getID() {
    return id;
  }

  @Override
  public Class<U> getType() {
    return variableClass;
  }

  @Override
  public int getAxis() {
    return axis;
  }

  private byte[] concat(byte[] left, byte[] right) {
    byte[] bytes = new byte[left.length + right.length];
    System.arraycopy(left, 0, bytes, 0, left.length);
    System.arraycopy(right, 0, bytes, left.length, right.length);
    return bytes;
  }

  @Override
  public U get() {
    VariableIdentifier variableID = new VariableIdentifier(id, axis, bank);

    byte[] outputBytes = controller.getConverter(VariableIdentifier.class).toBytes(variableID);

    byte[] inputBytes = controller.executeCopleyCommand(GET_VARIABLE, outputBytes);

    return controller.getConverter(variableClass).toObject(inputBytes);
  }

  public void set(U output) {
    VariableIdentifier variableID = new VariableIdentifier(id, axis, bank);

    byte[] outputBytes = concat(
        controller.getConverter(VariableIdentifier.class).toBytes(variableID),
        controller.getConverter(variableClass).toBytes(output));

    controller.executeCopleyCommand(SET_VARIABLE, outputBytes);
  }

  public void copyToBank() {
    VariableIdentifier variableID = new VariableIdentifier(id, axis, bank);

    byte[] outputBytes = controller.getConverter(VariableIdentifier.class).toBytes(variableID);

    controller.executeCopleyCommand(COPY_VARIABLE, outputBytes);
  }

  @Override
  public VariableBank getBank() {
    return bank;
  }
}
