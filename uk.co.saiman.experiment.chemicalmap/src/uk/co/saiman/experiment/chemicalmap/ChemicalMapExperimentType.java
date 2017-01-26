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

import static uk.co.strangeskies.utilities.Observable.Observation.TERMINATE;

import java.util.stream.Stream;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.experiment.ExperimentExecutionContext;
import uk.co.saiman.experiment.ExperimentResultType;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.instrument.raster.RasterDevice;
import uk.co.strangeskies.reflection.token.TypeToken;
import uk.co.strangeskies.text.properties.PropertyLoader;

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
public abstract class ChemicalMapExperimentType<T extends ChemicalMapConfiguration> implements ExperimentType<T> {
	private ChemicalMapProperties properties;
	private final ExperimentResultType<ChemicalMap> chemicalMapResult;

	public ChemicalMapExperimentType() {
		this(PropertyLoader.getDefaultProperties(ChemicalMapProperties.class));
	}

	public ChemicalMapExperimentType(ChemicalMapProperties properties) {
		this.properties = properties;
		this.chemicalMapResult = new ExperimentResultType<ChemicalMap>() {
			@Override
			public String getName() {
				return properties.chemicalMapResultName().toString();
			}

			@Override
			public TypeToken<ChemicalMap> getDataType() {
				return new TypeToken<ChemicalMap>() {};
			}
		};
	}

	/*
	 * TODO this really should be moved to the constructor, and the 'properties'
	 * and 'spectrumResult' fields should both be final ... hurry up OSGi r7 to
	 * sort this mess out
	 */
	protected void setProperties(ChemicalMapProperties properties) {
		this.properties = properties;
	}

	protected ChemicalMapProperties getProperties() {
		return properties;
	}

	@Override
	public String getName() {
		return properties.chemicalMapExperimentName().toString();
	}

	public ExperimentResultType<ChemicalMap> getChemicalMapResult() {
		return chemicalMapResult;
	}

	protected abstract RasterDevice getRasterDevice();

	protected abstract AcquisitionDevice getAcquisitionDevice();

	@Override
	public void execute(ExperimentExecutionContext<T> context) {
		getAcquisitionDevice().startEvents().addTerminatingObserver(device -> {
			startAcquisition(context, device);
			return TERMINATE;
		});
		getRasterDevice().startEvents().addTerminatingObserver(device -> {
			startRaster(context, device);
			new Thread(() -> getAcquisitionDevice().startAcquisition()).start();
			return TERMINATE;
		});

		getRasterDevice().startRasterOperation();

		context.getResult(chemicalMapResult).getData().get().save();
	}

	private void startRaster(ExperimentExecutionContext<T> context, RasterDevice device) {

	}

	public void startAcquisition(ExperimentExecutionContext<T> context, AcquisitionDevice device) {

	}

	@Override
	public Stream<ExperimentResultType<?>> getResultTypes() {
		return Stream.of(chemicalMapResult);
	}
}
