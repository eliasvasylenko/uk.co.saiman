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

import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;
import static uk.co.saiman.experiment.osgi.ExperimentServiceConstants.EXECUTOR_ID;
import static uk.co.saiman.experiment.variables.VariableCardinality.REQUIRED;
import static uk.co.saiman.maldi.spectrum.MaldiSpectrumConstants.SPECTRUM_ACQUISITION_COUNT;
import static uk.co.saiman.maldi.spectrum.MaldiSpectrumConstants.SPECTRUM_EXECUTOR;
import static uk.co.saiman.maldi.spectrum.MaldiSpectrumConstants.SPECTRUM_MASS_LIMIT;
import static uk.co.saiman.measurement.Units.dalton;

import javax.measure.Unit;
import javax.measure.quantity.Mass;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.PlanningContext;
import uk.co.saiman.experiment.spectrum.SpectrumExecutor;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.saiman.maldi.sample.SampleAreaHold;
import uk.co.saiman.maldi.spectrum.MaldiSpectrumExecutor.MaldiSpectrumExecutorConfiguration;

@Designate(ocd = MaldiSpectrumExecutorConfiguration.class, factory = true)
@Component(
    configurationPid = MaldiSpectrumExecutor.CONFIGURATION_PID,
    configurationPolicy = OPTIONAL,
    service = { MaldiSpectrumExecutor.class, SpectrumExecutor.class, Executor.class },
    property = EXECUTOR_ID + "=" + SPECTRUM_EXECUTOR)
public class MaldiSpectrumExecutor implements SpectrumExecutor {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Maldi Spectrum Experiment Executor",
      description = "The experiment executor which manages acquisition of spectra")
  public @interface MaldiSpectrumExecutorConfiguration {}

  public static final String CONFIGURATION_PID = SPECTRUM_EXECUTOR + ".impl";

  @Override
  public Unit<Mass> getMassUnit() {
    return dalton().getUnit();
  }

  @Override
  public Class<AcquisitionDevice> acquisitionDevice() {
    return AcquisitionDevice.class;
  }

  @Override
  public Class<SampleAreaHold> samplePreparation() {
    return SampleAreaHold.class;
  }

  @Override
  public void plan(PlanningContext context) {
    context.declareVariable(SPECTRUM_MASS_LIMIT, REQUIRED);
    context.declareVariable(SPECTRUM_ACQUISITION_COUNT, REQUIRED);
    SpectrumExecutor.super.plan(context);
  }
}
