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
package uk.co.saiman.experiment.procedure;

import java.util.function.Function;
import java.util.function.Supplier;

import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.product.Result;
import uk.co.saiman.state.StateMap;

/**
 * The context of an experiment node's initial configuration. When a workspace
 * is requested to create an experiment node of a given type, this context is
 * instantiated and passed to the experiment type implementation via
 * {@link Conductor#configureExperiment(ConfigurationContext)}.
 * <p>
 * In other words, each step has only one {@link ConfigurationContext} associated
 * with it when it is created. The configuration context remains valid so long
 * as the experiment node remains in the workspace, and references may be held
 * to it.
 * 
 * @author Elias N Vasylenko
 */
public interface ConfigurationContext {
  /**
   * Get the ID of the node.
   * 
   * @return the ID of the node, or an empty optional if it has not yet been set
   */
  String getId();

  /**
   * Set the ID of the node. The ID must be unique amongst all sibling nodes of
   * the same {@link Conductor experiment type}.
   * <p>
   * Typically the ID may be used to determine the location of {@link #getState()
   * persisted state} of an experiment, and so changing the ID may result in the
   * movement or modification of data.
   * 
   * @param id the ID for the node
   */
  void setId(String id);

  /**
   * This map represents the state of the experiment node associated with this
   * configuration context. This data should be persisted by the workspace
   * according to the format of an experiment file.
   * <p>
   * There is no standard enforced for the format of the value strings.
   * <p>
   * The execution of an experiment should generally not affect its persisted
   * state, directly or otherwise.
   * 
   * @return a map containing persisted key/value pairs
   */
  StateMap getState();

  void update(StateMap state);

  default void update(Function<? super StateMap, ? extends StateMap> function) {
    update(function.apply(getState()));
  }

  <T> DependencyHandle<Result<T>> setRequiredResult(
      Requirement<Result<T>> requirement,
      Dependency<? extends Result<? extends T>> dependency);

  <T> DependencyHandle<Result<T>> addRequiredResult(
      Requirement<Result<T>> requirement,
      Dependency<? extends Result<? extends T>> dependency);

  boolean removeRequiredResult(ResultRequirement<?> requirement, ProductPath dependency);

  boolean clearRequiredResults(ResultRequirement<?> requirement);

  /**
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * TODO
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * are dependencies actually stored at this level? Perhaps they should be
   * transiently created by the conductor when it initializes the variables
   * object. This way we have to wait until our entire procedure is initialized
   * and it's much easier to keep track of interdependencies when constructing our
   * procedure.
   * 
   * TODO this may also solve the problem of e.g. dependencies with peculiar
   * requirements, e.g. an experiment step which always needs to have a
   * requirement on an earlier sibling which is the product of a different child,
   * etc.
   * 
   * TODO we need a generic mechanism for experiment steps to report errors.
   * Obvious place for new API is in ProcedureContext.
   * 
   * 
   * 
   * TODO how do we handle renames? If an experiment step is renamed, how do we
   * notify any experiments which have an indirect dependency on it?
   * 
   * 
   * 
   * 
   * 
   * 
   * Indirect dependencies between experiments are generated transiently when we
   * initialize our variables, meaning conductors are individually responsible for
   * loading them, saving them, and validating them.
   * 
   * Think about how to express API for this control via ProcedureContext
   * 
   * How do we notify an experiment when such dependencies are moved, so that it
   * can decide if they're still valid and update its state? Perhaps when we
   * register an indirect dependency we are returned a handle on it which notifies
   * of changes...
   * 
   * An experiment step may wish to issue a warning through the ProcedureContext
   * if a certain product it makes has no children. It may also wish to disallow
   * being submitted if it has no children. (E.g. a stage move becomes pointless.)
   * How does it detect when children are added and removed? Again, fetch a
   * listener through the context.
   * 
   * 
   * 
   * 
   */
}
