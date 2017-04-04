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

import static java.util.function.Function.identity;

import java.io.IOException;
import java.nio.file.Path;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import uk.co.saiman.data.ContinuousFunction;
import uk.co.saiman.experiment.CachingObservableResource;
import uk.co.saiman.experiment.CachingResource;

public class FileSpectrum<C extends ContinuousFunction<Time, Dimensionless>> implements Spectrum {
	private final CachingResource<C> data;

	private final Path location;
	private final ByteFormat<C> format;

	protected FileSpectrum(Path location, String name, C data, ByteFormat<C> format) {
		this.data = new CachingObservableResource<>(this::load, this::save, identity());

		this.location = location.resolve(name + "." + format.getPathExtension());
		this.format = format;

		this.data.setData(data);
	}

	public Path getLocation() {
		return location;
	}

	@Override
	public void complete() {
		data.save();
	}

	protected C load() {
		try {
			return format.load(location);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void save(C data) {
		try {
			format.save(location, data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ContinuousFunction<Time, Dimensionless> getRawData() {
		return data.getData();
	}

	@Override
	public ContinuousFunction<Mass, Dimensionless> getCalibratedData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpectrumCalibration getCalibration() {
		// TODO Auto-generated method stub
		return null;
	}
}
