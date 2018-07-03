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
package uk.co.saiman.comms.copley.simulation;

import uk.co.saiman.bytes.conversion.ByteConverter;
import uk.co.saiman.comms.copley.VariableBank;

abstract class ComputedVariable<T> implements SimulatedVariable {
  private final ByteConverter<T> converter;

  public ComputedVariable(ByteConverter<T> converter) {
    this.converter = converter;
  }

  @Override
  public byte[] get(int axis, VariableBank bank) {
    switch (bank) {
    case ACTIVE:
      return converter.toBytes(compute(axis));
    default:
      throw new UnsupportedOperationException();
    }
  }

  public abstract T compute(int axis);

  @Override
  public void set(int axis, VariableBank bank, byte[] value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void copy(byte axis, VariableBank bank) {
    throw new UnsupportedOperationException();
  }
}