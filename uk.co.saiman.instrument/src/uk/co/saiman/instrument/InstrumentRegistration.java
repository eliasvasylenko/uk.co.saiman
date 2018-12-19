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

/**
 * A registration of a device with an instrument. An instrument registration is
 * created when a device is {@link Instrument#registerDevice(Device) registered}
 * to an instrument. It should be owned by the instrument, and may be obtained
 * via {@link Instrument#getRegistrations()} or
 * {@link Device#getInstrumentRegistration()}.
 * <p>
 * Each instance is mirrored by a corresponding {@link DeviceRegistration},
 * which is owned by the device.
 * <p>
 * An instance of {@link DeviceRegistration} cannot be obtained from an
 * instrument of {@link InstrumentRegistration}. This is to ensure that device
 * registrations can be passed safely around by users of the instrument API,
 * while the power to unregister a device can remain properly encapsulated by
 * the caller and implementor of {@link Instrument#registerDevice(Device)}.
 * 
 * @author Elias N Vasylenko
 */
public interface InstrumentRegistration {
  boolean isRegistered();

  Instrument getInstrument();

  Device<?> getDevice();
}
