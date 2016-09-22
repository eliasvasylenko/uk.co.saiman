/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import org.osgi.service.component.annotations.Activate;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.data.ContinuousFunction;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentResultType;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.strangeskies.reflection.TypeToken;
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
public abstract class SpectrumExperimentType<T extends SpectrumConfiguration> implements ExperimentType<T> {
	private SpectraProperties properties;
	private ExperimentResultType<T, ContinuousFunction> spectrumResult;

	public SpectrumExperimentType() {
		this(PropertyLoader.getDefaultProperties(SpectraProperties.class));
	}

	public SpectrumExperimentType(SpectraProperties properties) {
		this.properties = properties;
	}

	/*
	 * TODO this really should be moved to the constructor, and the 'properties'
	 * and 'spectrumResult' fields should both be final ... hurry up OSGi r7 to
	 * sort this mess out
	 */
	@Activate
	public void activate() {
		spectrumResult = new ExperimentResultType<>(properties.spectrumResultName().toString(), this,
				new TypeToken<ContinuousFunction>() {});
	}

	protected SpectrumConfiguration createStateImpl(ExperimentNode<?, ? extends T> forNode) {
		return new SpectrumConfiguration() {
			@Override
			public String getSpectrumName() {
				return getProperties().defaultSpectrumName().toString();
			}
		};
	}

	protected void setProperties(SpectraProperties properties) {
		this.properties = properties;
	}

	protected SpectraProperties getProperties() {
		return properties;
	}

	@Override
	public String getName() {
		return properties.spectrumExperimentName().toString();
	}

	protected abstract AcquisitionDevice getAcquisitionDevice();

	@Override
	public void execute(ExperimentNode<?, ? extends T> node) {
		ContinuousFunction accumulation = ContinuousFunction.EMPTY;

		getAcquisitionDevice().startAcquisition(o -> o.addObserver(System.out::println));

		node.setResult(spectrumResult, accumulation);
	}

	@Override
	public Stream<ExperimentResultType<T, ?>> getResultTypes() {
		return Stream.of(spectrumResult);
	}
}
