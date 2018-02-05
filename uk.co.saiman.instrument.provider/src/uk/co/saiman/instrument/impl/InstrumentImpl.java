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
 * This file is part of uk.co.saiman.instrument.provider.
 *
 * uk.co.saiman.instrument.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.impl;

import static uk.co.saiman.instrument.InstrumentLifecycleState.BEGIN_OPERATION;
import static uk.co.saiman.instrument.InstrumentLifecycleState.END_OPERATION;
import static uk.co.saiman.instrument.InstrumentLifecycleState.OPERATING;
import static uk.co.saiman.instrument.InstrumentLifecycleState.STANDBY;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.InstrumentLifecycleState;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;

/**
 * Reference implementation of {@link Instrument}, as an OSGi service.
 * 
 * If more than one instrument exists within the framework, each instance is
 * responsible for making sure it contains the correct devices. This may be
 * achieved by e.g. filtering on device components or by subsystem isolation.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class InstrumentImpl implements Instrument {
  private final Set<Device> devices;
  private final ObservableProperty<InstrumentLifecycleState> state;

  /**
   * Create an empty instrument in standby.
   */
  public InstrumentImpl() {
    devices = new HashSet<>();
    state = new ObservablePropertyImpl<>(STANDBY);
  }

  @Override
  public Stream<Device> getDevices() {
    return devices.stream();
  }

  @Override
  public synchronized Disposable addDevice(Device device) {
    devices.add(device);
    return () -> devices.remove(device);
  }

  @Override
  public synchronized void requestOperation() {
    if (!state.isEqual(OPERATING))
      if (transitionToState(BEGIN_OPERATION))
        transitionToState(OPERATING);
      else
        transitionToState(STANDBY);
  }

  @Override
  public synchronized void requestStandby() {
    if (state.isEqual(OPERATING))
      if (transitionToState(END_OPERATION))
        transitionToState(STANDBY);
  }

  private synchronized boolean transitionToState(InstrumentLifecycleState state) {
    this.state.set(state);
    return this.state.isValid();
  }

  @Override
  public ObservableValue<InstrumentLifecycleState> lifecycleState() {
    return state;
  }
}
