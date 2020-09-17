/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.copley.provider.
 *
 * uk.co.saiman.copley.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.copley.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.impl;

import static uk.co.saiman.comms.copley.CopleyVariableID.ACTUAL_POSITION;
import static uk.co.saiman.comms.copley.CopleyVariableID.AMPLIFIER_STATE;
import static uk.co.saiman.comms.copley.CopleyVariableID.DRIVE_EVENT_STATUS;
import static uk.co.saiman.comms.copley.CopleyVariableID.LATCHED_EVENT_STATUS;
import static uk.co.saiman.comms.copley.CopleyVariableID.MOTOR_ENCODER_ANGULAR_RESOLUTION;
import static uk.co.saiman.comms.copley.CopleyVariableID.MOTOR_ENCODER_DIRECTION;
import static uk.co.saiman.comms.copley.CopleyVariableID.MOTOR_ENCODER_LINEAR_RESOLUTION;
import static uk.co.saiman.comms.copley.CopleyVariableID.MOTOR_ENCODER_UNITS;
import static uk.co.saiman.comms.copley.CopleyVariableID.TRAJECTORY_POSITION_COUNTS;
import static uk.co.saiman.comms.copley.CopleyVariableID.TRAJECTORY_PROFILE_MODE;
import static uk.co.saiman.comms.copley.VariableBank.ACTIVE;
import static uk.co.saiman.comms.copley.VariableBank.STORED;

import java.util.LinkedHashMap;
import java.util.Map;

import uk.co.saiman.comms.copley.AmplifierState;
import uk.co.saiman.comms.copley.CopleyAxis;
import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.EventStatusRegister;
import uk.co.saiman.comms.copley.Int16;
import uk.co.saiman.comms.copley.Int32;
import uk.co.saiman.comms.copley.TrajectoryProfile;
import uk.co.saiman.comms.copley.Variable;
import uk.co.saiman.comms.copley.VariableBank;
import uk.co.saiman.comms.copley.WritableVariable;

public class CopleyAxisImpl implements CopleyAxis {
  private final CopleyNodeImpl controller;
  private final int axis;
  private final Map<CopleyVariableID, VariableImpl<?>> variables;

  public CopleyAxisImpl(CopleyNodeImpl controller, int axis) {
    this.controller = controller;
    this.axis = axis;
    this.variables = new LinkedHashMap<>();

    addVariable(DRIVE_EVENT_STATUS, EventStatusRegister.class, ACTIVE);
    addWritableVariable(LATCHED_EVENT_STATUS, EventStatusRegister.class, ACTIVE);
    addBankedVariable(TRAJECTORY_PROFILE_MODE, TrajectoryProfile.class);
    addBankedVariable(TRAJECTORY_POSITION_COUNTS, Int32.class);
    addBankedVariable(AMPLIFIER_STATE, AmplifierState.class);
    addWritableVariable(ACTUAL_POSITION, Int32.class, ACTIVE);
    addWritableVariable(MOTOR_ENCODER_UNITS, Int16.class, STORED);
    addWritableVariable(MOTOR_ENCODER_ANGULAR_RESOLUTION, Int32.class, STORED);
    addWritableVariable(MOTOR_ENCODER_LINEAR_RESOLUTION, Int16.class, STORED);
    addWritableVariable(MOTOR_ENCODER_DIRECTION, Int16.class, STORED);
  }

  @Override
  public int getAxisNumber() {
    return axis;
  }

  private <U> BankedVariableImpl<U> addBankedVariable(CopleyVariableID id, Class<U> type) {
    BankedVariableImpl<U> variable = new BankedVariableImpl<>(controller, id, type, axis);
    variables.put(id, variable);
    return variable;
  }

  private <U> WritableVariableImpl<U> addWritableVariable(
      CopleyVariableID id,
      Class<U> type,
      VariableBank bank) {
    WritableVariableImpl<U> variable = new WritableVariableImpl<>(controller, id, type, axis, bank);
    variables.put(id, variable);
    return variable;
  }

  private <U> VariableImpl<U> addVariable(CopleyVariableID id, Class<U> type, VariableBank bank) {
    VariableImpl<U> variable = new VariableImpl<>(controller, id, type, axis, bank);
    variables.put(id, variable);
    return variable;
  }

  @Override
  public Variable<?> variable(CopleyVariableID id) {
    return variables.get(id);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Variable<Int32> actualPosition() {
    return (Variable<Int32>) variables.get(ACTUAL_POSITION);
  }

  @SuppressWarnings("unchecked")
  @Override
  public WritableVariable<Int32> requestedPosition() {
    return (WritableVariable<Int32>) variables.get(TRAJECTORY_POSITION_COUNTS);
  }
}
