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

import static uk.co.saiman.experiment.state.Accessor.listAccessor;

import java.util.Random;

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
import uk.co.saiman.experiment.AnalysisProcedure;
import uk.co.saiman.experiment.ExperimentContext;
import uk.co.saiman.experiment.ResultRequirement;
import uk.co.saiman.experiment.Observation;
import uk.co.saiman.experiment.Procedure;
import uk.co.saiman.experiment.processing.Processing;
import uk.co.saiman.experiment.processing.ProcessingService;
import uk.co.saiman.experiment.state.Accessor;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 */
@Component(service = Procedure.class)
public class SpectrumProcessingProcedure
    implements AnalysisProcedure<SpectrumProcessingConfiguration, Spectrum, Spectrum> {
  public static final String PROCESSED_SPECTRUM = "uk.co.saiman.experiment.spectrum.processed.result";

  private final Accessor<Processing, ?> processorList;

  private final ResultRequirement<Spectrum> inputSpectrum;
  private final Observation<Spectrum> processedSpectrum;

  @Activate
  public SpectrumProcessingProcedure(@Reference ProcessingService processors) {
    this.processorList = listAccessor(
        "processing",
        processors::loadProcessing,
        processors::saveProcessing);
    this.inputSpectrum = new ResultRequirement<Spectrum>() {};
    this.processedSpectrum = new Observation<Spectrum>(PROCESSED_SPECTRUM) {};
  }

  @Override
  public SpectrumProcessingConfiguration configureVariables(
      ExperimentContext<SpectrumProcessingConfiguration> context) {
    context.update(s -> s.withDefault(processorList, Processing::new));

    return new SpectrumProcessingConfiguration() {
      private String name = context.getId(() -> "test-" + new Random().nextInt(Integer.MAX_VALUE));

      @Override
      public void setSpectrumName(String name) {
        this.name = name;
        context.setId(name);
      }

      @Override
      public String getSpectrumName() {
        return name;
      }

      @Override
      public Processing getProcessing() {
        return context.stateMap().get(processorList);
      }

      @Override
      public void setProcessing(Processing processing) {
        context.update(state -> state.with(processorList, processing));
      }
    };
  }

  @Override
  public Spectrum process(SpectrumProcessingConfiguration state, Spectrum input) {
    DataProcessor processor = state.getProcessing().getProcessor();

    SampledContinuousFunction<Mass, Dimensionless> massData = processor
        .process(input.getMassData());

    return new Spectrum() {
      @Override
      public ContinuousFunction<Time, Dimensionless> getTimeData() {
        return input.getTimeData();
      }

      @Override
      public DataProcessor getProcessing() {
        return input.getProcessing().andThen(processor);
      }

      @Override
      public SampledContinuousFunction<Mass, Dimensionless> getMassData() {
        return massData;
      }

      @Override
      public SpectrumCalibration getCalibration() {
        return input.getCalibration();
      }
    };
  }

  @Override
  public ResultRequirement<Spectrum> input() {
    return inputSpectrum;
  }

  @Override
  public Observation<Spectrum> output() {
    return processedSpectrum;
  }
}
