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

import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.instrument.acquisition.AcquisitionController;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.saiman.instrument.raster.RasterController;
import uk.co.saiman.instrument.raster.RasterDevice;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 */
public abstract class ChemicalMapExecutor implements Executor {
  protected abstract Provision<AcquisitionController> getAcquisitionControlResource();

  protected abstract Provision<AcquisitionDevice<?>> getAcquisitionDeviceResource();

  protected abstract Provision<RasterController> getRasterControlResource();

  protected abstract Provision<RasterDevice<?>> getRasterDeviceResource();;

  @Override
  public void execute(ExecutionContext context) {
    var acquisitionController = context.acquireDependency(getAcquisitionControlResource()).value();
    var acquisitionDevice = context.acquireDependency(getAcquisitionDeviceResource()).value();

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
