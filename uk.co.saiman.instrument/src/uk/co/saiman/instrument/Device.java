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

import java.util.Optional;
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
 * device is therefore provided via a {@link #acquireControl() control
 * interface}, which can only be acquired by one consumer at a time, acting as a
 * write-lock over the functions of the device.
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
 * 
 * @param <T> the control interface for the device
 */
public interface Device<T extends Controller> {
  /**
   * @return the human-readable and localized name of the device
   */
  String getName();

  /**
   * Immediately acquire a mutually exclusive lock on control of the device. The
   * returned interface should provide access to all control and
   * write-functionality of the device, but should not be required for reading
   * from the device.
   * 
   * @return an interface for interaction with the device
   */
  default Optional<? extends T> acquireControl() throws IllegalStateException {
    try {
      return Optional.of(acquireControl(0, TimeUnit.MILLISECONDS));
    } catch (TimeoutException | InterruptedException | IllegalStateException e) {
      return Optional.empty();
    }
  }

  T acquireControl(long timeout, TimeUnit unit) throws TimeoutException, InterruptedException;

  /**
   * Devices should only ever be registered to a single instrument, and must
   * always be registered when created. This method should therefore always return
   * the same registration, even if the registration subsequently becomes
   * {@link InstrumentRegistration#isRegistered() invalid} due to
   * {@link DeviceRegistration#deregister() deregistration}.
   * 
   * @return the instrument registration for the device
   */
  InstrumentRegistration getInstrumentRegistration();

  /**
   * Get an observable value over the state of the connection to the hardware
   * device.
   * 
   * @return the connection state
   */
  ObservableValue<DeviceStatus> status();
}
