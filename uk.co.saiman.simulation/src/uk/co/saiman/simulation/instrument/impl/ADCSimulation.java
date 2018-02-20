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

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.util.Random;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.acquisition.AcquisitionBufferPool;
import uk.co.saiman.data.function.SampledDomain;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.simulation.instrument.DetectorSimulation;
import uk.co.saiman.simulation.instrument.DetectorSimulationService;
import uk.co.saiman.simulation.instrument.SimulatedSampleSource;
import uk.co.saiman.simulation.instrument.impl.ADCSimulation.ADCSimulationConfiguration;

/**
 * A simulation of an acquisition data signal from an ADC.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = ADCSimulationConfiguration.class, factory = true)
@Component(configurationPid = ADCSimulation.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class ADCSimulation implements DetectorSimulationService {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "ADC Simulation Configuration",
      description = "The ADC simulation provides an implementation of a detector interface simulating an analogue-to-digital converter")
  public @interface ADCSimulationConfiguration {
    @AttributeDefinition(name = "SNR", description = "Set the simulated signal-to-noise-ratio")
    double signalToNoiseRatio() default 0.95;
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.simulation.adc";

  @Reference
  SimulatedSampleSource sampleSource;

  @Reference
  Units units;

  private double signalToNoise;

  private final Random random = new Random();

  @Activate
  @Modified
  void configure(ADCSimulationConfiguration configuration) {
    signalToNoise = configuration.signalToNoiseRatio();
  }

  @Override
  public DetectorSimulation getDetectorSimulation(
      SampledDomain<Time> domain,
      Unit<Dimensionless> intensityUnits) {
    AcquisitionBufferPool bufferPool = new AcquisitionBufferPool(domain, intensityUnits);

    return () -> bufferPool.fillNextBuffer(intensities -> {
      double scale = 0;
      double scaleDelta = 1d / domain.getDepth();

      for (int j = 0; j < intensities.length; j++) {
        scale += scaleDelta;
        intensities[j] = 0.01
            + scale * (1 - scale + random.nextDouble() * Math.max(0, (int) (scale * 20) % 4 - 1));
      }
    });
  }
}
