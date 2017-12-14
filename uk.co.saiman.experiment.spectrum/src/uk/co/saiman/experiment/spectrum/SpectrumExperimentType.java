/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static uk.co.saiman.data.spectrum.SpectrumProcessor.identity;
import static uk.co.saiman.text.properties.PropertyLoader.getDefaultProperties;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.data.CachingData;
import uk.co.saiman.data.Data;
import uk.co.saiman.data.spectrum.ContinuousFunctionAccumulator;
import uk.co.saiman.data.spectrum.SampledSpectrum;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.data.spectrum.SpectrumProcessor;
import uk.co.saiman.data.spectrum.format.RegularSampledSpectrumFormat;
import uk.co.saiman.experiment.ExecutionContext;
import uk.co.saiman.experiment.ExperimentType;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of sample configuration for the instrument
 */
public abstract class SpectrumExperimentType<T extends SpectrumConfiguration>
    implements ExperimentType<T, Spectrum> {
  private static final String SPECTRUM_DATA_NAME = "spectrum";

  private SpectrumProperties properties;

  public SpectrumExperimentType() {
    this(getDefaultProperties(SpectrumProperties.class));
  }

  /*
   * TODO this parameter really should be injected by DS. Hurry up OSGi r7 to make
   * this possible ...
   */
  public SpectrumExperimentType(SpectrumProperties properties) {
    this.properties = properties;
  }

  protected void setProperties(SpectrumProperties properties) {
    this.properties = properties;
  }

  protected SpectrumProperties getProperties() {
    return properties;
  }

  @Override
  public String getName() {
    return properties.spectrumExperimentName().toString();
  }

  protected abstract AcquisitionDevice getAcquisitionDevice();

  @Override
  public Spectrum execute(ExecutionContext<T, Spectrum> context) {
    AcquisitionDevice device = getAcquisitionDevice();

    ContinuousFunctionAccumulator<Time, Dimensionless> accumulator = new ContinuousFunctionAccumulator<>(
        device.acquisitionDataEvents(),
        device.getSampleDomain(),
        device.getSampleIntensityUnits());

    Data<Spectrum> data = context
        .setResult(
            new CachingData<Spectrum>(
                context.getLocation(),
                SPECTRUM_DATA_NAME,
                new RegularSampledSpectrumFormat(null)) {

            });

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
     */
    data
        .set(
            new SampledSpectrum(
                accumulator.accumulation().getNext().join().revalidate(),
                null,
                context
                    .node()
                    .getState()
                    .getProcessing()
                    .stream()
                    .map(SpectrumProcessorType::getProcessor)
                    .reduce(identity(), SpectrumProcessor::andThen)));

    device.startAcquisition();

    return data.get();
  }
}
