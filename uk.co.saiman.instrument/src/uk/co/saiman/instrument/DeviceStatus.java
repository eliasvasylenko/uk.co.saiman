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
 * This file is part of uk.co.saiman.instrument.
 *
 * uk.co.saiman.instrument is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument;

public enum DeviceStatus {
  /**
   * The device is currently available for control by a client.
   */
  AVAILABLE,

  /**
   * The device is currently under control by another client.
   */
  UNAVAILABLE,

  /**
   * The device is currently not accessible, due to some failure in connection or
   * operation.
   * <p>
   * When the device enters this state, any acquired control of the device must be
   * released. Subsequent attempting to acquire control of the device may still
   * succeed, depending on the implementation and the reason for inaccessibility,
   * in which case the device will transition to the {@link #UNAVAILABLE} state.
   */
  INACCESSIBLE,

  /**
   * The device is currently not accessible, due to being disposed. This state is
   * terminal.
   */
  DISPOSED
}
