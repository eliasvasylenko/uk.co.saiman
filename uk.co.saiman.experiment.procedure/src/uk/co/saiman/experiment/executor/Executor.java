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
package uk.co.saiman.experiment.executor;

import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.source.Production;
import uk.co.saiman.experiment.requirement.AdditionalRequirement;
import uk.co.saiman.experiment.requirement.Requirement;
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
 */
public interface Executor {
  /**
   * It is required that productions be returned in the order in which they are
   * fulfilled, as this ordering is used to validate that experiment step
   * interdependencies are satisfiable.
   * 
   * @return the productions made by this executor
   */
  Stream<? extends Production<?>> products();

  Requirement<?> mainRequirement();

  Stream<AdditionalRequirement<?>> additionalRequirements();

  /**
   * @return the set of required variables for experiments executed by this
   *         executor
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
   * @param instruction the instruction to execute
   * @param context     the execution context
   */
  void execute(ExecutionContext context);
}
