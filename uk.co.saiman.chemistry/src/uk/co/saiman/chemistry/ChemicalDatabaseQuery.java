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
 * This file is part of uk.co.saiman.chemistry.
 *
 * uk.co.saiman.chemistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.chemistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.chemistry;

import java.util.Set;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

import uk.co.saiman.mathematics.Interval;

/**
 * An interface providing a view over some chemical database.
 * 
 * @author Elias N Vasylenko
 */
public interface ChemicalDatabaseQuery {
  /**
   * @return the set of chemicals which match the query
   */
  Set<Chemical> findChemicals();

  ChemicalDatabaseQuery withMass(Quantity<Mass> mass, double relativeErrorMargin);

  ChemicalDatabaseQuery withMass(Interval<Quantity<Mass>> massRange);

  ChemicalDatabaseQuery containingElements(ChemicalComposition composition);

  /**
   * Structural information may only optionally be provided by a database, so by
   * default this returns the empty query.
   * 
   * @return a derived query limiting to chemicals which contain the given
   *         structure
   */
  default ChemicalDatabaseQuery containingSubStructure(/*- TODO ChemicalStructure structure */) {
    return EmptyChemicalDatabaseQuery.instance();
  }
}
