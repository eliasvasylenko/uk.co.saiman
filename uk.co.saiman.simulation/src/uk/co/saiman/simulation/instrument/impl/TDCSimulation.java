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

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.util.Random;

import javax.measure.Quantity;
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

import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.data.SampledDomain;
import uk.co.saiman.data.SparseSampledContinuousFunction;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.simulation.instrument.DetectorSimulation;
import uk.co.saiman.simulation.instrument.impl.TDCSimulation.TDCSimulationConfiguration;

/**
 * A simulation of an acquisition data signal from a TDC.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = TDCSimulationConfiguration.class, factory = true)
@Component(configurationPid = TDCSimulation.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class TDCSimulation implements DetectorSimulation {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "TDC Simulation Configuration",
      description = "The TDC simulation provides an implementation of a detector interface simulating a time-to-digital converter")
  public @interface TDCSimulationConfiguration {
    @AttributeDefinition(
        name = "Acquisition Resolution",
        description = "The minimum resolvable units of time for samples")
    String acquisitionResolution() default SimulatedAcquisitionDevice.DEFAULT_ACQUISITION_RESOLUTION_SECONDS
        + "s";

    @AttributeDefinition(
        name = "Maximum Hits Per Spectrum",
        description = "Set the maximum number of hit events per detector event")
    int maximumHitsPerSpectrum() default 10;
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.simulation.tdc";

  @Reference
  Instrument instrument;

  @Reference
  Units units;

  private int maximumHits = 10;
  private int[] hitIndices;
  private double[] hitIntensities;
  private Quantity<Time> resolution;

  private final Random random = new Random();

  @Activate
  @Modified
  void configure(TDCSimulationConfiguration configuration) {
    maximumHits = configuration.maximumHitsPerSpectrum();
    resolution = units.parseQuantity(configuration.acquisitionResolution()).asType(Time.class);
  }

  @Override
  public Instrument getInstrument() {
    return instrument;
  }

  @Override
  public Quantity<Time> getSampleResolution() {
    return resolution;
  }

  private int updateMaximumHitsPerSpectrum() {
    int currentMaximumHits = hitIndices.length;

    if (currentMaximumHits != maximumHits) {
      currentMaximumHits = maximumHits;

      hitIndices = new int[currentMaximumHits];
      hitIntensities = new double[currentMaximumHits];
    }

    return currentMaximumHits;
  }

  @Override
  public SampledContinuousFunction<Time, Dimensionless> acquire(
      SampledDomain<Time> domain,
      Unit<Dimensionless> intensityUnits) {
    int hits = random.nextInt(updateMaximumHitsPerSpectrum());

    /*
     * TODO distribute "hits" number of hits
     */

    return new SparseSampledContinuousFunction<>(
        domain,
        intensityUnits,
        hits,
        hitIndices,
        hitIntensities);
  }
}
