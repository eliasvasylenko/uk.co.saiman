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
 * This file is part of uk.co.saiman.instrument.acquisition.adq.
 *
 * uk.co.saiman.instrument.acquisition.adq is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.acquisition.adq is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.acquisition.adq;

import static uk.co.saiman.instrument.acquisition.adq.AdqProductId.ADQ114;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface Adq114Device extends AdqDevice {
  @Override
  Adq114Control acquireControl(long timeout, TimeUnit unit)
      throws TimeoutException, InterruptedException;

  @Override
  default AdqProductId getProductId() {
    return ADQ114;
  }

  Adq114DataFormat getDataFormat();

  int getPllDivider();

  int getAccumulationsPerAcquisition();

  public interface Adq114Control extends AdqControl {
    void setPllDivider(int pllDivider);

    void setAccumulationsPerAcquisition(int accumulations);
  }
}
