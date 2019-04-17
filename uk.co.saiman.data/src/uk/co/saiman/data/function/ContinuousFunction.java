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

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * A set of domain values (X) mapped via some continuous function to a
 * corresponding value in the range (Y). The domain is defined by a single
 * inclusive interval, and the function is defined for all values in the domain.
 * <p>
 * TODO: Difficult to genericise over data type with acceptable performance
 * until Project Valhalla, for now will just use double.
 * 
 * @author Elias N Vasylenko
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 */
public interface ContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>> {
  /**
   * @param unitDomain
   *          the units of the domain
   * @param unitRange
   *          the units of the range
   * @return a simple, immutable instance of a continuous function describing a
   *         single point at 0 in both the domain and codomain according to the
   *         given units
   */
  static <UD extends Quantity<UD>, UR extends Quantity<UR>> ContinuousFunction<UD, UR> empty(
      Unit<UD> unitDomain,
      Unit<UR> unitRange) {
    return new EmptyContinuousFunction<>(unitDomain, unitRange);
  }

  Domain<UD> domain();

  Range<UR> range();

  /**
   * Find the resulting Y value of the function for a given X.
   * 
   * @param domainPosition
   *          The X value to resolve
   * @return The Y value for the given X
   */
  double sample(double domainPosition);

  /**
   * Sometimes it is useful to linearize a continuous function in order to
   * perform complex processing, or for display purposes. This method provides a
   * linear view of any continuous function. Implementations may remove features
   * which are unresolvable at the given resolution.
   * 
   * <p>
   * Note that the parameter only represents the <em>resolvable</em> domain and
   * the contained samples may not be exactly represented in the domain of the
   * output. For example an implementation may retain some sub-resolution
   * features to indicate maxima and minima within a resolvable interval, such
   * that thin peaks and troughs don't disappear from visual representation at
   * low resolution.
   * 
   * @param resolvableSampleDomain
   *          a representation of the resolvable sample domain
   * @return a new sampled, linear continuous function roughly equal to the
   *         receiver to the requested resolution
   */
  SampledContinuousFunction<UD, UR> resample(SampledDomain<UD> resolvableSampleDomain);
}
