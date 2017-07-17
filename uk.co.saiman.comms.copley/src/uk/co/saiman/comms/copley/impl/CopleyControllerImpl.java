/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.comms.copley.
 *
 * uk.co.saiman.comms.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley is distributed in the hope that it will be useful,
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
import static uk.co.saiman.comms.copley.CopleyVariableID.TRAJECTORY_POSITION_COUNTS;
import static uk.co.saiman.comms.copley.CopleyVariableID.TRAJECTORY_PROFILE_MODE;
import static uk.co.saiman.comms.copley.VariableBank.ACTIVE;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import uk.co.saiman.comms.ByteConverters;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.CopleyOperationID;
import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.MotorAxis;
import uk.co.saiman.comms.copley.Variable;
import uk.co.saiman.comms.copley.VariableBank;

public class CopleyControllerImpl implements CopleyController {
  private CopleyCommsImpl comms;
  private final ByteConverters converters;

  private final Map<CopleyVariableID, VariableImpl<?>> variables;

  private final VariableImpl<EventStatusRegister> driveEventStatus;
  private final WritableVariableImpl<EventStatusRegister> latchedEventStatus;
  private final BankedVariableImpl<TrajectoryProfile> trajectoryProfile;
  private final BankedVariableImpl<Int32> trajectoryPosition;
  private final BankedVariableImpl<AmplifierState> amplifierState;
  private final WritableVariableImpl<Int32> actualPosition;

  public CopleyControllerImpl(CopleyCommsImpl comms) {
    this.comms = comms;
    this.converters = comms.getConverters();

    variables = new LinkedHashMap<>();
    driveEventStatus = addVariable(DRIVE_EVENT_STATUS, EventStatusRegister.class, ACTIVE);
    latchedEventStatus = addWritableVariable(
        LATCHED_EVENT_STATUS,
        EventStatusRegister.class,
        ACTIVE);
    trajectoryProfile = addBankedVariable(TRAJECTORY_PROFILE_MODE, TrajectoryProfile.class);
    trajectoryPosition = addBankedVariable(TRAJECTORY_POSITION_COUNTS, Int32.class);
    amplifierState = addBankedVariable(AMPLIFIER_STATE, AmplifierState.class);
    actualPosition = addWritableVariable(ACTUAL_POSITION, Int32.class, ACTIVE);
  }

  public ByteConverters getConverters() {
    return converters;
  }

  public void close() {
    comms = null;
  }

  private <U> BankedVariableImpl<U> addBankedVariable(CopleyVariableID id, Class<U> type) {
    BankedVariableImpl<U> variable = new BankedVariableImpl<>(this, id, type);
    variables.put(id, variable);
    return variable;
  }

  private <U> WritableVariableImpl<U> addWritableVariable(
      CopleyVariableID id,
      Class<U> type,
      VariableBank bank) {
    WritableVariableImpl<U> variable = new WritableVariableImpl<>(this, id, type, bank);
    variables.put(id, variable);
    return variable;
  }

  private <U> VariableImpl<U> addVariable(CopleyVariableID id, Class<U> type, VariableBank bank) {
    VariableImpl<U> variable = new VariableImpl<>(this, id, type, bank);
    variables.put(id, variable);
    return variable;
  }

  @Override
  public Variable<?> getVariable(CopleyVariableID id) {
    return variables.get(id);
  }

  @Override
  public Stream<MotorAxis> getAxes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MotorAxis getAxis(int axis) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getAxisCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  byte[] executeCopleyCommand(CopleyOperationID operation, byte[] output) {
    CopleyCommsImpl comms = this.comms;
    if (comms == null) {
      throw new IllegalStateException("Copley comms controller was reset");
    }
    return comms.executeCopleyCommand(operation, output);
  }
}
