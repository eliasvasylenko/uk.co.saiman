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
 * This file is part of uk.co.saiman.maldi.spectrum.
 *
 * uk.co.saiman.maldi.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.spectrum;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.experiment.service.ExperimentServiceConstants.EXECUTOR_ID;
import static uk.co.saiman.maldi.acquisition.MaldiAcquisitionConstants.MALDI_ACQUISITION_CONTROLLER;
import static uk.co.saiman.maldi.acquisition.MaldiAcquisitionConstants.MALDI_ACQUISITION_DEVICE;
import static uk.co.saiman.maldi.sample.MaldiSamplePlateExecutor.PLATE_SUBMISSION;
import static uk.co.saiman.measurement.Units.dalton;

import javax.measure.Unit;
import javax.measure.quantity.Mass;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.dependency.source.Preparation;
import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.spectrum.SpectrumExecutor;
import uk.co.saiman.instrument.acquisition.AcquisitionController;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.saiman.maldi.spectrum.MaldiSpectrumExecutor.MaldiSpectrumExecutorConfiguration;
import uk.co.saiman.maldi.stage.SamplePlateSubmission;

@Designate(ocd = MaldiSpectrumExecutorConfiguration.class, factory = true)
@Component(configurationPid = MaldiSpectrumExecutor.EXECUTOR_ID, configurationPolicy = REQUIRE, service = {
    MaldiSpectrumExecutor.class,
    SpectrumExecutor.class,
    Executor.class }, property = EXECUTOR_ID + "=" + MaldiSpectrumExecutor.EXECUTOR_ID)
public class MaldiSpectrumExecutor implements SpectrumExecutor {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Maldi Spectrum Experiment Executor", description = "The experiment executor which manages acquisition of spectra")
  public @interface MaldiSpectrumExecutorConfiguration {}

  public static final String EXECUTOR_ID = "uk.co.saiman.maldi.spectrum.executor";

  public static final String MALDI_SPECTRUM = "uk.co.saiman.maldi.spectrum.result";

  @Override
  public Unit<Mass> getMassUnit() {
    return dalton().getUnit();
  }

  @Override
  public Provision<AcquisitionDevice<?>> acquisitionDevice() {
    return MALDI_ACQUISITION_DEVICE;
  }

  @Override
  public Provision<AcquisitionController> acquisitionControl() {
    return MALDI_ACQUISITION_CONTROLLER;
  }

  @Override
  public Preparation<SamplePlateSubmission> samplePreparation() {
    return PLATE_SUBMISSION;
  }
}
