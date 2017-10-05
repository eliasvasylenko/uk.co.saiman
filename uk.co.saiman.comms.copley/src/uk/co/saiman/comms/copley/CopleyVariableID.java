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
package uk.co.saiman.comms.copley;

public enum CopleyVariableID {
  DRIVE_EVENT_STATUS(0xA1),
  LATCHED_EVENT_STATUS(0xA1),
  TRAJECTORY_PROFILE_MODE(0xC8),
  TRAJECTORY_POSITION_COUNTS(0xCA),
  AMPLIFIER_STATE(0x24),
  ACTUAL_POSITION(0x17);

  private final int code;

  private CopleyVariableID(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public static CopleyVariableID forCode(byte code) {
    for (CopleyVariableID variable : values())
      if (variable.getCode() == (0xFF & code))
        return variable;

    throw new IllegalArgumentException("No Copley variable matches code " + code);
  }
}
