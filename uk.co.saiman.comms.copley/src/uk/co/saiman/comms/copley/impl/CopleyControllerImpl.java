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

import static uk.co.saiman.comms.copley.CopleyOperationID.GET_VARIABLE;
import static uk.co.saiman.comms.copley.CopleyVariableID.DRIVE_EVENT_STATUS;
import static uk.co.saiman.comms.copley.ErrorCode.ILLEGAL_AXIS_NUMBER;
import static uk.co.saiman.comms.copley.VariableBank.ACTIVE;

import java.util.ArrayList;
import java.util.List;

import uk.co.saiman.comms.ByteConverter;
import uk.co.saiman.comms.ByteConverters;
import uk.co.saiman.comms.copley.CopleyAxis;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.CopleyOperationID;
import uk.co.saiman.comms.copley.VariableIdentifier;

public class CopleyControllerImpl implements CopleyController {
  private static final int MAXIMUM_AXES = 8;

  private CopleyCommsImpl comms;
  private final ByteConverters converters;

  private final List<CopleyAxis> axes;

  public CopleyControllerImpl(CopleyCommsImpl comms) {
    this.comms = comms;
    this.converters = comms.getConverters();
    int axisCount = countAxes();
    this.axes = new ArrayList<>(axisCount);
    for (int i = 0; i < axisCount; i++) {
      axes.add(new CopleyAxisImpl(this, i));
    }
  }

  private int countAxes() {
    int axes = 0;
    do {
      try {
        executeCopleyCommand(
            GET_VARIABLE,
            getConverter(VariableIdentifier.class)
                .toBytes(new VariableIdentifier(DRIVE_EVENT_STATUS, axes, ACTIVE)));

      } catch (CopleyErrorException e) {
        if (e.getCode() == ILLEGAL_AXIS_NUMBER) {
          return axes;
        } else {
          throw e;
        }
      }
    } while (++axes < MAXIMUM_AXES);
    return axes;
  }

  public <T> ByteConverter<T> getConverter(Class<T> type) {
    return converters.getConverter(type);
  }

  public void close() {
    comms = null;
  }

  @Override
  public int getAxisCount() {
    return axes.size();
  }

  @Override
  public CopleyAxis getAxis(int i) {
    return axes.get(i);
  }

  byte[] executeCopleyCommand(CopleyOperationID operation, byte[] output) {
    CopleyCommsImpl comms = this.comms;
    if (comms == null) {
      throw new IllegalStateException("Copley comms controller was reset");
    }
    return comms.executeCopleyCommand(operation, output);
  }
}
