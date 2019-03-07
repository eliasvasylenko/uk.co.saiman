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

import java.util.stream.Stream;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.data.spectrum.ContinuousFunctionAccumulator;
import uk.co.saiman.data.spectrum.SampledSpectrum;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.data.spectrum.SpectrumCalibration;
import uk.co.saiman.data.spectrum.format.RegularSampledSpectrumFormat;
import uk.co.saiman.experiment.procedure.ConditionRequirement;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.ConductionContext;
import uk.co.saiman.experiment.product.Condition;
import uk.co.saiman.experiment.product.Observation;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of sample configuration for the instrument
 */
public interface SpectrumConductor<T extends SpectrumConfiguration>
    extends Conductor<T, Condition<Void>> {
  static final String SPECTRUM_DATA_NAME = "spectrum";

  Unit<Mass> getMassUnit();

  AcquisitionDevice<?> getAcquisitionDevice();

  Observation<Spectrum> spectrumObservation();

  ConditionRequirement<Void> sampleResource();

  @Override
  default void conduct(ConductionContext<T, Condition<Void>> context) {
    System.out.println("create accumulator");
    AcquisitionDevice<?> device = getAcquisitionDevice();
    ContinuousFunctionAccumulator<Time, Dimensionless> accumulator = new ContinuousFunctionAccumulator<>(
        device.acquisitionDataEvents(),
        device.getSampleDomain(),
        device.getSampleIntensityUnits());

    System.out.println("prepare processing");
    DataProcessor processing = context.variables().getProcessing().getProcessor();

    System.out.println("fetching calibration");
    SpectrumCalibration calibration = new SpectrumCalibration() {
      @Override
      public Unit<Time> getTimeUnit() {
        return device.getSampleTimeUnits();
      }

      @Override
      public Unit<Mass> getMassUnit() {
        return SpectrumConductor.this.getMassUnit();
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
                    spectrumObservation(),
                    o.map(s -> new SampledSpectrum(s, calibration, processing))::revalidate));

    System.out.println("start acquisition");

    try (var control = device.acquireControl()) {
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
      var accumulation = accumulator.getAccumulation();

      context
          .setResultFormat(
              spectrumObservation(),
              SPECTRUM_DATA_NAME,
              new RegularSampledSpectrumFormat(null));
      context
          .setResult(
              spectrumObservation(),
              new SampledSpectrum(accumulation, calibration, processing));
    }
  }

  @Override
  default ConditionRequirement<Void> requirement() {
    return sampleResource();
  }

  @Override
  default Stream<Observation<?>> observations() {
    return Stream.of(spectrumObservation());
  }
}
