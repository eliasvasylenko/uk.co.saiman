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

import static uk.co.saiman.experiment.processing.Processing.PROCESSING_VARIABLE;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.function.ContinuousFunction;
import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.data.spectrum.SpectrumCalibration;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.PlanningContext;
import uk.co.saiman.experiment.variables.VariableCardinality;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 */
@Component(service = Executor.class)
public class SpectrumProcessingExecutor implements Executor {
  public static final String OUTPUT_SPECTRUM = "uk.co.saiman.experiment.spectrum.processing.output";

  @Override
  public void plan(PlanningContext context) {
    context.declareMainRequirement(SpectrumExecutor.SPECTRUM);
    context.declareVariable(PROCESSING_VARIABLE, VariableCardinality.OPTIONAL);
    context.declareProduct(SpectrumExecutor.SPECTRUM);
  }

  @Override
  public void execute(ExecutionContext context) {
    DataProcessor processor = context.getVariable(PROCESSING_VARIABLE).getProcessor();

    context
        .acquireDependency(SpectrumExecutor.SPECTRUM)
        .updates()
        .reduceBackpressure((a, b) -> b)
        .requestNext()
        .partialMap(i -> i.value())
        .map(s -> processSpectrum(processor, s))
        .thenRequestNext()
        .then(r -> context.observePartialResult(SpectrumExecutor.SPECTRUM, () -> r))
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
