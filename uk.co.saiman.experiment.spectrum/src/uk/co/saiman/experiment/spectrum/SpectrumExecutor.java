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

import static java.util.concurrent.TimeUnit.SECONDS;
import static uk.co.saiman.experiment.processing.ProcessingDeclaration.PROCESSING_VARIABLE;

import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.data.spectrum.ContinuousFunctionAccumulator;
import uk.co.saiman.data.spectrum.SampledSpectrum;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.data.spectrum.SpectrumCalibration;
import uk.co.saiman.data.spectrum.format.RegularSampledSpectrumFormat;
import uk.co.saiman.experiment.instruction.ExecutionContext;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.instruction.ExecutorException;
import uk.co.saiman.experiment.instruction.IndirectRequirements;
import uk.co.saiman.experiment.processing.ProcessingService;
import uk.co.saiman.experiment.production.Condition;
import uk.co.saiman.experiment.production.Observation;
import uk.co.saiman.experiment.production.Production;
import uk.co.saiman.experiment.requirement.ConditionRequirement;
import uk.co.saiman.experiment.variables.VariableDeclaration;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 */
public interface SpectrumExecutor extends Executor<Condition<Void>> {
  String SPECTRUM_DATA_NAME = "spectrum";
  Observation<Spectrum> SPECTRUM = new Observation<>("uk.co.saiman.data.spectrum", Spectrum.class);

  Unit<Mass> getMassUnit();

  AcquisitionDevice<?> getAcquisitionDevice();

  ConditionRequirement<Void> sampleResource();

  ProcessingService processingService();

  @Override
  default void execute(ExecutionContext<Condition<Void>> context) {
    System.out.println("create accumulator");
    AcquisitionDevice<?> device = getAcquisitionDevice();
    ContinuousFunctionAccumulator<Time, Dimensionless> accumulator = new ContinuousFunctionAccumulator<>(
        device.acquisitionDataEvents(),
        device.getSampleDomain(),
        device.getSampleIntensityUnit());

    System.out.println("prepare processing");
    DataProcessor processing = context
        .getVariable(PROCESSING_VARIABLE)
        .load(processingService())
        .getProcessor();

    System.out.println("fetching calibration");
    SpectrumCalibration calibration = new SpectrumCalibration() {
      @Override
      public Unit<Time> getTimeUnit() {
        return device.getSampleTimeUnit();
      }

      @Override
      public Unit<Mass> getMassUnit() {
        return SpectrumExecutor.this.getMassUnit();
      }

      @Override
      public double getMass(double time) {
        return time;
      }
    };

    System.out.println("attach observer");
    accumulator
        .accumulation()
        .observe(
            o -> context
                .setPartialResult(
                    SPECTRUM,
                    () -> new SampledSpectrum(o.getLatestAccumulation(), calibration, processing)));

    System.out.println("start acquisition");

    try (var control = device.acquireControl(2, SECONDS)) {
      control.startAcquisition();

      /*
       * TODO some sort of invalidate/lazy-revalidate message passer
       * 
       * ContinuousFunctionAccumulator already has this, it provides an observable
       * with backpressure which gives the latest spectrum every time it is requested
       * and otherwise does no work (i.e. no array copying etc.). The limitation is
       * that it can't notify a listener when a new item is actually available without
       * actually doing the work and sending an item, the listener has to just request
       * and see.
       * 
       * The problem is how to pass this through the Result API to users without
       * losing the laziness so we can request at e.g. the monitor refresh rate.
       * 
       * Perhaps the observable type should be `Result<T>` rather than `T`?
       */

      System.out.println("get result");
      var accumulation = accumulator.getCompleteAccumulation();

      context.setResultFormat(SPECTRUM, SPECTRUM_DATA_NAME, new RegularSampledSpectrumFormat(null));
      context.setResult(SPECTRUM, new SampledSpectrum(accumulation, calibration, processing));
    } catch (TimeoutException | InterruptedException e) {
      throw new ExecutorException("Failed to acquire control of acquisition device", e);
    }
  }

  @Override
  default ConditionRequirement<Void> directRequirement() {
    return sampleResource();
  }

  @Override
  default Stream<Production<?>> products() {
    return Stream.of(SPECTRUM);
  }

  @Override
  default Stream<VariableDeclaration> variables() {
    return Stream.of(PROCESSING_VARIABLE.declareRequired());
  }

  @Override
  default Stream<IndirectRequirements> indirectRequirements() {
    return Stream.empty();
  }
}
