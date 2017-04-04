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

import java.nio.file.Path;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import uk.co.saiman.data.RegularSampledDomain;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.experiment.spectrum.RegularSampledContinuousFunctionByteFormat;
import uk.co.saiman.experiment.spectrum.RegularSampledDomainByteFormat;

public class AccumulatingFileChemicalMap extends
		FileChemicalMap<RegularSampledDomain<Time>, SampledContinuousFunction<Time, Dimensionless>> {
	public AccumulatingFileChemicalMap(
			Path location,
			String name,
			int width,
			int height,
			RegularSampledDomain<Time> domain,
			Unit<Dimensionless> rangeUnit) {
		super(
				location,
				name,
				width,
				height,
				domain,
				new RegularSampledDomainByteFormat<>(domain.getUnit()),
				d -> RegularSampledContinuousFunctionByteFormat.overDomain(d, rangeUnit));
	}
}
