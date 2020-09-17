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
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.eclipse.ui.TransferFormat;

public class TransferIn {
  private final Map<TransferFormat<?>, Supplier<?>> values;

  public TransferIn() {
    this.values = Map.of();
  }

  private TransferIn(Map<TransferFormat<?>, Supplier<?>> values) {
    this.values = values;
  }

  public <T> TransferIn with(TransferFormat<T> format, Supplier<T> value) {
    var values = new HashMap<>(this.values);
    values.put(format, value);
    return new TransferIn(values);
  }

  public <T> T getValue(TransferFormat<T> format) {
    @SuppressWarnings("unchecked")
    Supplier<T> value = (Supplier<T>) values.get(format);
    if (value == null) {
      throw new IllegalArgumentException();
    }
    return value.get();
  }

  public Stream<TransferFormat<?>> getTransferFormats() {
    return values.keySet().stream();
  }
}
