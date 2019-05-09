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
 * This file is part of uk.co.saiman.copley.
 *
 * uk.co.saiman.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley;

import uk.co.saiman.bytes.conversion.DTO;
import uk.co.saiman.bytes.conversion.Offset;
import uk.co.saiman.bytes.conversion.Size;

@DTO
public class VariableIdentifier {
  public VariableIdentifier() {}

  public VariableIdentifier(CopleyVariableID variable, int axis, VariableBank bank) {
    this.axis = (byte) axis;
    this.variableID = (byte) variable.getCode();
    this.bank = bank.getBit();
  }

  @Offset(0)
  public byte variableID;

  @Offset(12)
  public boolean bank;

  @Offset(13)
  @Size(value = 3)
  public byte axis;

  public VariableBank bank() {
    return VariableBank.fromBit(bank);
  }
}
