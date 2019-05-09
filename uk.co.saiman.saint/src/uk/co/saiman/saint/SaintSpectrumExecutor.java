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
 * This file is part of uk.co.saiman.saint.
 *
 * uk.co.saiman.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.saint;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static uk.co.saiman.measurement.Units.dalton;

import javax.measure.Unit;
import javax.measure.quantity.Mass;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.processing.ProcessingService;
import uk.co.saiman.experiment.requirement.ConditionRequirement;
import uk.co.saiman.experiment.requirement.Requirement;
import uk.co.saiman.experiment.sample.XYStageExecutor;
import uk.co.saiman.experiment.spectrum.SpectrumExecutor;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.saiman.saint.SaintSpectrumExecutor.SaintSpectrumExecutorConfiguration;

@Designate(ocd = SaintSpectrumExecutorConfiguration.class, factory = true)
@Component(configurationPid = SaintSpectrumExecutor.CONFIGURATION_PID, configurationPolicy = REQUIRE, service = {
    SaintSpectrumExecutor.class,
    SpectrumExecutor.class,
    Executor.class })
public class SaintSpectrumExecutor implements SpectrumExecutor {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Saint Spectrum Experiment Executor", description = "The experiment executor which manages acquisition of spectra")
  public @interface SaintSpectrumExecutorConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.saint.spectrumexecutor";

  public static final String SAINT_SPECTRUM = "uk.co.saiman.saint.spectrum.result";

  private final AcquisitionDevice<?> acquisitionDevice;
  private final ConditionRequirement<Void> sampleResource;
  private final ProcessingService processingService;

  @Activate
  public SaintSpectrumExecutor(
      @Reference XYStageExecutor stageExperiment,
      @Reference(cardinality = OPTIONAL) AcquisitionDevice<?> acquisitionDevice,
      @Reference ProcessingService processingService) {
    this.acquisitionDevice = acquisitionDevice;
    this.sampleResource = Requirement.on(stageExperiment.samplePreparation());
    this.processingService = processingService;
  }

  @Override
  public Unit<Mass> getMassUnit() {
    return dalton().getUnit();
  }

  @Override
  public AcquisitionDevice<?> getAcquisitionDevice() {
    return acquisitionDevice;
  }

  @Override
  public ConditionRequirement<Void> sampleResource() {
    return sampleResource;
  }

  @Override
  public ProcessingService processingService() {
    return processingService;
  }
}
