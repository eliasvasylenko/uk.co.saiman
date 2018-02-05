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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment;

/**
 * A representation of whether a constraint on the structure of and experiment
 * node tree is fulfilled.
 * <p>
 * Each node is optionally constrained with every one of its ancestor and
 * descendant nodes via the
 * {@link ExperimentType#mayComeBefore(ExperimentNode, ExperimentType)} and
 * {@link ExperimentType#mayComeAfter(ExperimentNode)} methods.
 * 
 * Taking the set of results of all relevant invocations for a given node type
 * and node graph location, the following conditions are applied to determine
 * whether a node fulfills its constraints and my be added to the graph:
 * 
 * <ul>
 * <li>If the set contains at least one instance of {@link #VIOLATED} then the
 * node <em>does not</em> fulfill its constraints.</li>
 * <li>Else if the set contains at least one instance of
 * {@link #ASSUME_ALL_FULFILLED} then the node fulfills its constraints.</li>
 * <li>Else if the set contains at least one instance of {@link #UNFULFILLED}
 * then the node <em>does not</em> fulfill its constraints.</li>
 * <li>Else the node fulfills its constraints.</li>
 * </ul>
 * 
 * @author Elias N Vasylenko
 */
public enum ExperimentNodeConstraint {
  /**
   * A negative condition is present
   */
  VIOLATED,
  /**
   * A positive condition is missing
   */
  UNFULFILLED,
  /**
   * All positive conditions are present
   */
  FULFILLED,
  /**
   * Missing positive conditions should be ignored
   */
  ASSUME_ALL_FULFILLED;
}
