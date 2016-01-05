/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.data.api.
 *
 * uk.co.saiman.data.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data;

import java.util.stream.DoubleStream;

import uk.co.strangeskies.mathematics.Range;

/**
 * TODO Difficult to genericise over data type with acceptable performance until
 * Project Valhalla, for now will just use double.
 * 
 * @author Elias N Vasylenko
 */
public interface Continuum {
	Range<Double> getXRange();

	Range<Double> getYRange();

	double sampleY(double xPosition);

	Range<Double> getYRange(double startX, double endX);

	default DoubleStream sampleYStream(Range<Double> between, double delta) {
		double from = between.getFrom();
		if (!between.isFromInclusive())
			from += delta;

		long count = (long) ((between.getTo() - from) / delta) + 1;
		if (!between.isToInclusive())
			count--;

		// TODO takeWhile with Java 9
		return DoubleStream.iterate(from, d -> d + delta).limit(count);
	}
}
