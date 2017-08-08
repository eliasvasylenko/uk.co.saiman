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

/**
 * Factory for generating normalised {@link PeakShapeFunction}s at a given
 * position, and of a given total intensity.
 * <p>
 * Peaks may differ in shape according to position, e.g. to model degrading
 * effective mass resolution at higher masses, though the change should be
 * continuous with change in position.
 * <p>
 * Peaks at higher positions should always return higher values for
 * {@link PeakShapeFunction#effectiveDomainStart()} and
 * {@link PeakShapeFunction#effectiveDomainEnd()}.
 * 
 * @author Elias N Vasylenko
 */
public interface PeakShapeFunctionFactory {
	/**
	 * @param mean
	 *          the position of the peak in the domain axis
	 * @param intensity
	 *          the total intensity of the peak, i.e. the integral over the peak
	 * @return a peak shape function for the given position and intensity
	 */
	PeakShapeFunction atPeakPosition(double mean, double intensity);
}
