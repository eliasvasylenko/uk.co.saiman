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
import static uk.co.saiman.experiment.requirement.Requirement.onCondition;
import static uk.co.saiman.experiment.variables.VariableCardinality.REQUIRED;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.data.spectrum.ContinuousFunctionAccumulator;
import uk.co.saiman.data.spectrum.SampledSpectrum;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.data.spectrum.SpectrumCalibration;
import uk.co.saiman.data.spectrum.format.RegularSampledSpectrumFormat;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.PlanningContext;
import uk.co.saiman.instrument.acquisition.AcquisitionController;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.saiman.log.Log.Level;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 */
public interface SpectrumExecutor extends Executor {
  String SPECTRUM_DATA_NAME = "spectrum";

  Unit<Mass> getMassUnit();

  Class<? extends AcquisitionDevice<?>> acquisitionDevice();

  Class<? extends AcquisitionController> acquisitionControl();

  Class<?> samplePreparation();

  @Override
  default void plan(PlanningContext context) {
    context.declareVariable(PROCESSING_VARIABLE, REQUIRED);
    context.declareMainRequirement(onCondition(samplePreparation()));
    context.declareResourceRequirement(acquisitionDevice());
    context.declareResourceRequirement(acquisitionControl());
    context.observesResult(Spectrum.class);
  }

  @Override
  default void execute(ExecutionContext context) {
    var device = context.acquireResource(acquisitionDevice()).value();

    context.log().log(Level.INFO, "preparing processing...");
    DataProcessor processing = context.getVariable(PROCESSING_VARIABLE).getProcessor();

    context.log().log(Level.INFO, "preparing calibration...");
    SpectrumCalibration calibration = SpectrumCalibration
        .withUnits(device.getSampleTimeUnit(), SpectrumExecutor.this.getMassUnit(), time -> time);

    SampledContinuousFunction<Time, Dimensionless> accumulation;

    context.log().log(Level.INFO, "holding sample...");
    try (var sample = context.acquireCondition(samplePreparation())) {
      context.log().log(Level.INFO, "acquired sample hold");

      context.log().log(Level.INFO, "creating accumulator...");

      ContinuousFunctionAccumulator<Time, Dimensionless> accumulator = new ContinuousFunctionAccumulator<>(
          device.acquisitionDataEvents(),
          device.getSampleDomain(),
          device.getSampleIntensityUnit());

      context.log().log(Level.INFO, "attaching observer...");
      accumulator
          .accumulation()
          .observe(
              o -> context
                  .observePartialResult(
                      Spectrum.class,
                      () -> new SampledSpectrum(
                          o.getLatestAccumulation(),
                          calibration,
                          processing)));

      context.log().log(Level.INFO, "starting acquisition...");
      var controller = context.acquireResource(acquisitionControl()).value();
      controller.startAcquisition();

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

      context.log().log(Level.INFO, "acquired result");
      accumulation = accumulator.getCompleteAccumulation();
    }

    context
        .setResultFormat(
            Spectrum.class,
            SPECTRUM_DATA_NAME,
            new RegularSampledSpectrumFormat(null));
    context
        .observeResult(Spectrum.class, new SampledSpectrum(accumulation, calibration, processing));
  }
}
