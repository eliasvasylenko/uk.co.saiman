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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.saiman.observable.ObservableValue;

/**
 * Typically this interface should be extended to provide interfaces over
 * general classes of hardware such that alternative hardware can be
 * substituted.
 * <p>
 * A physical hardware device, generally speaking, is inherently a singleton
 * service with shared state. Because of this it is important to regulate access
 * when control is contested between multiple consumers. Exclusive control of a
 * device is therefore provided via a {@link #acquireControl(long , TimeUnit )
 * control interface}, which can only be acquired by one consumer at a time,
 * acting as a write-lock over the functions of the device.
 * <p>
 * Reading from the device needs no such regulation and should be provided via
 * methods directly on the implementing class of the device. Sometimes it may be
 * useful or necessary to issue certain commands without owning control of the
 * device, such as to override and shutdown operation. Such methods may also be
 * provided on the implementing class of the device, but they should carefully
 * document that they usurp control from any currently acquired control
 * interface and explain the consequences of this.
 * 
 * @author Elias N Vasylenko
 */
public interface Device {
  Controller acquireControl(long timeout, TimeUnit unit) throws TimeoutException, InterruptedException;

  /**
   * Get an observable value over the state of the connection to the hardware
   * device.
   * 
   * @return the connection state
   */
  ObservableValue<DeviceStatus> status();

  ObservableValue<ControllerStatus> controllerStatus();
}
