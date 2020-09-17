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
 * This file is part of uk.co.saiman.experiment.spectrum.
 *
 * uk.co.saiman.experiment.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.spectrum;

import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;
import static uk.co.saiman.experiment.processing.Processing.PROCESSING_VARIABLE;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.data.function.ContinuousFunction;
import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.data.spectrum.SpectrumCalibration;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.PlanningContext;
import uk.co.saiman.experiment.osgi.ExperimentServiceConstants;
import uk.co.saiman.experiment.spectrum.SpectrumProcessingExecutor.SpectrumProcessingExecutorConfiguration;
import uk.co.saiman.experiment.variables.VariableCardinality;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = SpectrumProcessingExecutorConfiguration.class, factory = true)
@Component(
    configurationPid = SpectrumProcessingExecutor.CONFIGURATION_PID,
    configurationPolicy = OPTIONAL,
    property = ExperimentServiceConstants.EXECUTOR_ID
        + "="
        + SpectrumProcessingExecutor.SPECTRUM_PROCESSING_EXECUTOR)
public class SpectrumProcessingExecutor implements Executor {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Spectrum Processing Experiment Executor",
      description = "The experiment executor which manages processing of spectra")
  public @interface SpectrumProcessingExecutorConfiguration {}

  public static final String SPECTRUM_PROCESSING_EXECUTOR = "uk.co.saiman.executor.spectrum.processing";
  public static final String CONFIGURATION_PID = SPECTRUM_PROCESSING_EXECUTOR + ".impl";
  public static final String OUTPUT_SPECTRUM = "uk.co.saiman.experiment.spectrum.processing.output";

  @Override
  public void plan(PlanningContext context) {
    context.declareResultRequirement(Spectrum.class);
    context.declareVariable(PROCESSING_VARIABLE, VariableCardinality.OPTIONAL);
    context.observesResult(Spectrum.class);
  }

  @Override
  public void execute(ExecutionContext context) {
    DataProcessor processor = context.getVariable(PROCESSING_VARIABLE).getProcessor();

    context
        .acquireResult(Spectrum.class)
        .updates()
        .reduceBackpressure((a, b) -> b)
        .requestNext()
        .partialMap(i -> i.value())
        .map(s -> processSpectrum(processor, s))
        .thenRequestNext()
        .then(r -> context.observePartialResult(Spectrum.class, () -> r))
        .join();
  }

  public Spectrum processSpectrum(DataProcessor processor, Spectrum spectrum) {
    var massData = processor.process(spectrum.getMassData());

    return new Spectrum() {
      @Override
      public ContinuousFunction<Time, Dimensionless> getTimeData() {
        return spectrum.getTimeData();
      }

      @Override
      public DataProcessor getProcessing() {
        return spectrum.getProcessing().andThen(processor);
      }

      @Override
      public SampledContinuousFunction<Mass, Dimensionless> getMassData() {
        return massData;
      }

      @Override
      public SpectrumCalibration getCalibration() {
        return spectrum.getCalibration();
      }
    };
  }
}
