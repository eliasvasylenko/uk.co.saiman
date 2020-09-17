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

import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.co.saiman.data.format.DataFormat;

public class TransferFormat<T> {
  private final DataFormat<T> format;
  private final Set<TransferMode> transferModes;

  public TransferFormat(DataFormat<T> format, Collection<? extends TransferMode> transferModes) {
    this.format = format;
    this.transferModes = unmodifiableSet(new HashSet<>(transferModes));
  }

  public DataFormat<T> dataFormat() {
    return format;
  }

  /**
   * The transfer modes which a data target may accept in this format.
   * <p>
   * Note that this only determines the accepted transfer modes at the
   * <em>target</em>, and has no bearing on which transfer modes may be supported
   * by the <em>source</em>.
   * 
   * @return a set of the accepted transfer modes
   */
  public Set<TransferMode> transferModes() {
    return transferModes;
  }
}
