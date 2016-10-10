/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
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
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Activate;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.data.ArrayRegularSampledContinuousFunction;
import uk.co.saiman.data.ContinuousFunction;
import uk.co.saiman.data.ContinuousFunctionExpression;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentResultType;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.strangeskies.reflection.TypeToken;
import uk.co.strangeskies.text.properties.PropertyLoader;
import uk.co.strangeskies.utilities.AggregatingListener;

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
	private ExperimentResultType<ContinuousFunction<Dimensionless, Time>> spectrumResult;

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
				new TypeToken<ContinuousFunction<Dimensionless, Time>>() {});
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

	public ExperimentResultType<ContinuousFunction<Dimensionless, Time>> getSpectrumResult() {
		return spectrumResult;
	}

	protected abstract AcquisitionDevice getAcquisitionDevice();

	@Override
	public void execute(ExperimentNode<?, ? extends T> node) {
		Unit<Dimensionless> intensityUnits = getAcquisitionDevice().getSampleIntensityUnits();
		Unit<Time> timeUnits = getAcquisitionDevice().getSampleTimeUnits();

		ContinuousFunctionExpression<Dimensionless, Time> result = new ContinuousFunctionExpression<>(intensityUnits,
				timeUnits);
		node.setResult(spectrumResult, result);

		getAcquisitionDevice().startAcquisition(device -> {

			int depth = device.getAcquisitionDepth();
			double frequency = 1 / device.getAcquisitionResolution();

			ArrayRegularSampledContinuousFunction<Dimensionless, Time> accumulator = new ArrayRegularSampledContinuousFunction<>(
					intensityUnits, timeUnits, frequency, 0, new double[depth]);

			result.setComponent(accumulator);

			AggregatingListener<SampledContinuousFunction<Dimensionless, Time>> aggregate = new AggregatingListener<>();
			device.nextAcquisitionDataEvents().addObserver(aggregate);
			aggregate.addObserver(a -> {
				accumulator.mutate(data -> {
					for (SampledContinuousFunction<Dimensionless, Time> c : a) {
						for (int i = 0; i < depth; i++) {
							data[i] += c.getY(i);
						}
					}
				});
			});
		});
	}

	@Override
	public Stream<ExperimentResultType<?>> getResultTypes() {
		return Stream.of(spectrumResult);
	}
}
