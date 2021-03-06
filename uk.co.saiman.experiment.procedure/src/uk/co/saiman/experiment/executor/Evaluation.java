/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.procedure.
 *
 * uk.co.saiman.experiment.procedure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.procedure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.executor;

public enum Evaluation {
  /**
   * Evaluation of each dependent must occur during the same preparation, and must
   * also occur in order.
   */
  ORDERED,
  /**
   * Evaluation of each dependent must occur during the same preparation, but may
   * occur in any order.
   */
  SERIAL,
  /**
   * Evaluation of each dependent must occur during the same preparation, but may
   * occur in parallel or in any order.
   */
  PARALLEL,
  /**
   * Evaluation of each dependent may occur during separate preparations, and may
   * occur in parallel or in any order.
   * <p>
   * This is the least restrictive evaluation strategy, and is also the default
   * when none is explicitly declared.
   */
  SEPARATE
}
