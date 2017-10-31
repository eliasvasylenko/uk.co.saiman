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

import static uk.co.saiman.text.properties.PropertyLoader.getDefaultProperties;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.experiment.ExecutionContext;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.Resource;
import uk.co.saiman.experiment.ResultType;

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
  private final ResultType<AccumulatingFileSpectrum> spectrumResult;

  public SpectrumExperimentType() {
    this(getDefaultProperties(SpectrumProperties.class));
  }

  /*
   * TODO this parameter really should be injected by DS. Hurry up OSGi r7 to make
   * this possible ...
   */
  public SpectrumExperimentType(SpectrumProperties properties) {
    this.properties = properties;
    spectrumResult = new FileSpectrumExperimentResultType<T>(this);
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
  public AccumulatingFileSpectrum execute(ExperimentNode<?, T> node) {
    AcquisitionDevice device = getAcquisitionDevice();

    Future<AccumulatingFileSpectrum> acquisition = device
        .acquisitionDataEvents()
        .reduce(() -> initializeResult(context, device), (fileSpectrum, message) -> {
          fileSpectrum.accumulate(message);
          return fileSpectrum;
        });

    device.startAcquisition();

    return acquisition.get();
  }

  private AccumulatingFileSpectrum initializeResult(
      ExperimentNode<?, T> context,
      AcquisitionDevice device) {
    Resource resource = context.results().getResource(spectrumResult);

    AccumulatingFileSpectrum fileSpectrum = new AccumulatingFileSpectrum(
        resource,
        SPECTRUM_DATA_NAME,
        device.getSampleDomain(),
        device.getSampleIntensityUnits());

    context.results().set(spectrumResult, fileSpectrum);

    return fileSpectrum;
  }

  @Override
  public ResultType<AccumulatingFileSpectrum> getResultType() {
    return spectrumResult;
  }
}
