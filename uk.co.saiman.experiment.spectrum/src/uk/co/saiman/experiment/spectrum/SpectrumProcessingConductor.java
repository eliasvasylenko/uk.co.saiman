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

import static uk.co.saiman.experiment.processing.ProcessingDeclaration.PROCESSING_VARIABLE;

import java.util.stream.Stream;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.function.ContinuousFunction;
import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.data.spectrum.SpectrumCalibration;
import uk.co.saiman.experiment.procedure.ConductionContext;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.Requirement;
import uk.co.saiman.experiment.procedure.Requirements;
import uk.co.saiman.experiment.procedure.ResultRequirement;
import uk.co.saiman.experiment.processing.ProcessingService;
import uk.co.saiman.experiment.product.Production;
import uk.co.saiman.experiment.product.Result;
import uk.co.saiman.experiment.variables.VariableDeclaration;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 */
@Component(service = Conductor.class)
public class SpectrumProcessingConductor implements Conductor<Result<Spectrum>> {
  public static final String OUTPUT_SPECTRUM = "uk.co.saiman.experiment.spectrum.processing.output";

  private final ResultRequirement<Spectrum> inputSpectrum;
  private final ProcessingService processingService;

  @Activate
  public SpectrumProcessingConductor(@Reference ProcessingService processingService) {
    this.inputSpectrum = Requirement.on(SpectrumConductor.SPECTRUM);
    this.processingService = processingService;
  }

  @Override
  public void conduct(ConductionContext<Result<Spectrum>> context) {
    DataProcessor processor = processingService
        .loadDeclaration(context.getVariable(PROCESSING_VARIABLE))
        .getProcessor();

    context
        .dependency()
        .updates()
        .reduceBackpressure((a, b) -> b)
        .requestNext()
        .partialMap(i -> i.value())
        .map(s -> processSpectrum(processor, s))
        .thenRequestNext()
        .then(r -> context.setPartialResult(SpectrumConductor.SPECTRUM, () -> r))
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

  @Override
  public ResultRequirement<Spectrum> directRequirement() {
    return inputSpectrum;
  }

  @Override
  public Stream<Production<?>> products() {
    return Stream.of(SpectrumConductor.SPECTRUM);
  }

  @Override
  public Stream<VariableDeclaration> variables() {
    return Stream.of(PROCESSING_VARIABLE.declareOptional());
  }

  @Override
  public Stream<Requirements> indirectRequirements() {
    return Stream.empty();
  }
}
