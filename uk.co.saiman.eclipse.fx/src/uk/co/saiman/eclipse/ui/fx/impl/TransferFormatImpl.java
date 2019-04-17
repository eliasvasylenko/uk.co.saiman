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

import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.eclipse.ui.TransferFormat;
import uk.co.saiman.eclipse.ui.TransferMode;

class TransferFormatImpl<T> implements TransferFormat<T> {
  private final Set<TransferMode> transferModes;
  private final DataFormat<T> format;

  public TransferFormatImpl(
      Collection<? extends TransferMode> transferModes,
      DataFormat<T> format) {
    this.transferModes = unmodifiableSet(new HashSet<>(transferModes));
    this.format = format;
  }

  @Override
  public Set<TransferMode> transferModes() {
    return transferModes;
  }

  @Override
  public DataFormat<T> dataFormat() {
    return format;
  }
}