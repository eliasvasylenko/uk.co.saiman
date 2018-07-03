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

import static uk.co.saiman.comms.copley.VariableBank.ACTIVE;
import static uk.co.saiman.comms.copley.VariableBank.STORED;

import java.util.ArrayList;
import java.util.List;

import uk.co.saiman.bytes.conversion.ByteConverter;
import uk.co.saiman.comms.copley.VariableBank;

class ReferenceVariable<T> implements SimulatedVariable {
  private final ByteConverter<T> converter;
  private final List<T> active;
  private final List<T> defaults;

  public ReferenceVariable(int axes, ByteConverter<T> converter, T identity) {
    this.converter = converter;

    active = new ArrayList<>(axes);
    defaults = new ArrayList<>(axes);

    for (int i = 0; i < axes; i++) {
      active.add(identity);
      defaults.add(identity);
    }
  }

  ByteConverter<T> getConverter() {
    return converter;
  }

  public T getReference(int axis, VariableBank bank) {
    switch (bank) {
    case ACTIVE:
      return active.get(axis);
    case STORED:
      return defaults.get(axis);
    default:
      throw new AssertionError();
    }
  }

  public void setReference(int axis, VariableBank bank, T value) {
    switch (bank) {
    case ACTIVE:
      active.set(axis, value);
      break;
    case STORED:
      defaults.set(axis, value);
      break;
    default:
      throw new AssertionError();
    }
  }

  @Override
  public byte[] get(int axis, VariableBank bank) {
    return converter.toBytes(getReference(axis, bank));
  }

  @Override
  public void set(int axis, VariableBank bank, byte[] value) {
    setReference(axis, bank, converter.toObject(value));
  }

  @Override
  public void copy(byte axis, VariableBank bank) {
    set(axis, bank, get(axis, bank == ACTIVE ? STORED : ACTIVE));
  }
}
