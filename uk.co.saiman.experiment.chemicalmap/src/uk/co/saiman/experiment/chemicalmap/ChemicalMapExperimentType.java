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
 * This file is part of uk.co.saiman.experiment.chemicalmap.
 *
 * uk.co.saiman.experiment.chemicalmap is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.chemicalmap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.chemicalmap;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.experiment.ProcessingContext;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.instrument.raster.RasterDevice;
import uk.co.saiman.text.properties.PropertyLoader;

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
public abstract class ChemicalMapExperimentType<T extends ChemicalMapConfiguration>
    implements ExperimentType<T, ChemicalMap> {
  private static final String CHEMICAL_MAP_DATA_NAME = "chemicalmap";

  private final ChemicalMapProperties properties;

  public ChemicalMapExperimentType() {
    this(PropertyLoader.getDefaultProperties(ChemicalMapProperties.class));
  }

  public ChemicalMapExperimentType(ChemicalMapProperties properties) {
    this.properties = properties;
  }

  protected ChemicalMapProperties getProperties() {
    return properties;
  }

  @Override
  public String getName() {
    return properties.chemicalMapExperimentName().toString();
  }

  protected abstract RasterDevice getRasterDevice();

  protected abstract AcquisitionDevice getAcquisitionDevice();

  @Override
  public ChemicalMap process(ProcessingContext<T, ChemicalMap> context) {
    AcquisitionDevice acquisitionDevice = getAcquisitionDevice();
    RasterDevice rasterDevice = getRasterDevice();

    /*- TODO
    Consumer<Spectrum> writer = new CumulativeChemicalMapFormat()
        .getWriter(rasterDevice.getRasterPattern());
    
    CompletableFuture<SampledSpectrum> acquisition = acquisitionDevice
        .acquisitionDataEvents()
        .reduce(
            () -> new SampledSpectrum(
                context.results().getDataPath(),
                CHEMICAL_MAP_DATA_NAME,
                acquisitionDevice.getSampleDomain(),
                acquisitionDevice.getSampleIntensityUnits()),
            (fileSpectrum, message) -> {
              fileSpectrum.accumulate(message);
              return fileSpectrum;
            });
    
    startAcquisition();
    
    return acquisition.join();
    
    context.results().get(chemicalMapResult).get().save();
    */
    throw new UnsupportedOperationException();
  }

  protected abstract void startAcquisition();
}
