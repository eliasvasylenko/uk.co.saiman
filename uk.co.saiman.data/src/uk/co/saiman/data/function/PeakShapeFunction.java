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
 * This file is part of uk.co.saiman.data.
 *
 * uk.co.saiman.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data.function;

/**
 * An interface for describing a peak shape function which can be sampled over
 * an effective area. The peak shape may be generated from samples, or it may be
 * defined mathematically as e.g. a Gaussian or Lorentz distribution.
 * 
 * <p>
 * A peak is assumed to have negligible intensity at the
 * {@link #effectiveDomainStart() start} and {@link #effectiveDomainEnd() end}
 * of its effective domain, and only a single {@link #maximum() local maximum}.
 * 
 * @author Elias N Vasylenko
 */
public interface PeakShapeFunction {
	/**
	 * @param value
	 *          the input value in the domain to sample the result in the codomain
	 * @return the output of the function at the given input
	 */
	double sample(double value);

	/**
	 * @return the point in the domain at which the function is at its maximum
	 *         result
	 */
	double maximum();

	/**
	 * @return the mean centre of the peak
	 */
	double mean();

	/**
	 * @return the width between
	 */
	double fullWidthAtHalfMaximum();

	/**
	 * @return the lowest useful value beyond which intensity is negligible
	 */
	double effectiveDomainStart();

	/**
	 * @return the highest useful value beyond which intensity is negligible
	 */
	double effectiveDomainEnd();
}
