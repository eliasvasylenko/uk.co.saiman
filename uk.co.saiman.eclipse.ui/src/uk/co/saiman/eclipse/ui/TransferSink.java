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
package uk.co.saiman.eclipse.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TransferSink {
  private final Map<TransferFormat<?>, Consumer<?>> values;

  public TransferSink() {
    this.values = Map.of();
  }

  private TransferSink(Map<TransferFormat<?>, Consumer<?>> values) {
    this.values = values;
  }

  public <T> TransferSink with(TransferFormat<T> format, Consumer<T> value) {
    var values = new HashMap<>(this.values);
    values.put(format, value);
    return new TransferSink(values);
  }

  @SuppressWarnings("unchecked")
  public <T> void putValue(TransferFormat<T> format, T value) {
    if (!values.containsKey(format)) {
      throw new IllegalArgumentException();
    }
    ((Consumer<T>) values.get(format)).accept(value);
  }

  public Stream<TransferFormat<?>> getTransferFormats() {
    return values.keySet().stream();
  }
}
