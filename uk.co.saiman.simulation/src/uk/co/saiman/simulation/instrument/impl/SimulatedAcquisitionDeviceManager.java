/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.instrument.impl;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.instrument.HardwareDevice;
import uk.co.saiman.log.Log;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.simulation.SimulationProperties;
import uk.co.saiman.simulation.instrument.DetectorSimulation;
import uk.co.saiman.text.properties.PropertyLoader;

/**
 * Partial implementation of a simulation of an acquisition device.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class SimulatedAcquisitionDeviceManager {
  private BundleContext context;
  private final Map<DetectorSimulation, ServiceRegistration<?>> serviceRegistrations = new HashMap<>();
  private final Set<DetectorSimulation> detectors = new HashSet<>();

  @Reference
  private Log log;

  @Reference
  Units units;
  private Unit<Dimensionless> intensityUnits;
  private Unit<Time> timeUnits;

  @Reference
  PropertyLoader loader;
  private SimulationProperties simulationText;

  @Activate
  synchronized void activate(BundleContext context) {
    this.context = context;

    simulationText = loader.getProperties(SimulationProperties.class);

    intensityUnits = units.count().get();
    timeUnits = units.second().get();

    detectors.stream().forEach(detector -> register(detector));
  }

  @Reference(policy = DYNAMIC, cardinality = MULTIPLE)
  synchronized void addComms(DetectorSimulation detector) {
    try {
      this.detectors.add(detector);

      if (context != null) {
        register(detector);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  synchronized void removeComms(DetectorSimulation comms) {
    this.detectors.remove(comms);
    ServiceRegistration<?> restService = serviceRegistrations.remove(comms);
    if (restService != null) {
      restService.unregister();
    }
  }

  void register(DetectorSimulation detector) {
    AcquisitionDevice acquisitionDevice = new SimulatedAcquisitionDevice(this, detector);

    serviceRegistrations.put(
        detector,
        context.registerService(
            new String[] { AcquisitionDevice.class.getName(), HardwareDevice.class.getName() },
            acquisitionDevice,
            null));
  }

  Log getLog() {
    return log;
  }

  Units getUnits() {
    return units;
  }

  SimulationProperties getText() {
    return simulationText;
  }

  Unit<Time> getTimeUnits() {
    return timeUnits;
  }

  Unit<Dimensionless> getIntensityUnits() {
    return intensityUnits;
  }
}
