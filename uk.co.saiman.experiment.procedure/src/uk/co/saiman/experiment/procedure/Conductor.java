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
package uk.co.saiman.experiment.procedure;

import java.util.stream.Stream;

import uk.co.saiman.experiment.product.Dependency;
import uk.co.saiman.experiment.product.Production;
import uk.co.saiman.experiment.variables.VariableDeclaration;

/**
 * An implementation of this interface represents a type of experiment node
 * which can appear in an experiment, for example "Spectrum", "Chemical Map",
 * "Stage Position", etc. Only one instance of such an implementation typically
 * needs to be registered with any given workspace.
 * 
 * <p>
 * The experiment type instance contains methods for managing the state and
 * processing of nodes of its type.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the {@link #directRequirement() direct requirement}
 */
public interface Conductor<T extends Dependency> {
  /**
   * It is required that productions be returned in the order in which they are
   * fulfilled, as this ordering is used to validate that experiment step
   * interdependencies are satisfiable.
   * 
   * @return the productions made by this conductor
   */
  Stream<Production<?>> products();

  Requirement<T> directRequirement();

  Stream<Requirements> indirectRequirements();

  /**
   * @return the set of required variables for experiments conducted by this
   *         conductor
   */
  Stream<VariableDeclaration> variables();

  /*
   * Execution is cheap and resources are unlimited, so we may proceed
   * automatically when necessary
   */
  default boolean isAutomatic() {
    return false;
  }

  /**
   * Process this experiment type for a given node.
   * 
   * @param context the processing context
   */
  void conduct(ConductionContext<T> context);
}
