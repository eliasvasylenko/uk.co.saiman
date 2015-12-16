/*
 * Copyright (C) 2015 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.acquisition.
 *
 * uk.co.saiman.acquisition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.acquisition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.acquisition;

import java.util.List;

import uk.co.strangeskies.mathematics.Range;

public interface AcquisitionGates {
	AcquisitionModule acquisition();

	double getStartTime();

	void setStartTime(double newStartTime);

	List<Range<Double>> getPassGates();

	void setPassGates(List<Range<Double>> gates);

	List<Range<Double>> getFailGates();

	void setFailGates(List<Range<Double>> gates);
}
