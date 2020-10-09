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
 * This file is part of uk.co.saiman.experiment.definition.
 *
 * uk.co.saiman.experiment.definition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.definition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.design;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.ProcedureException;

/**
 * An experiment design is an immutable description of an experiment, which may
 * be used to generate a machine-consumable {@link Procedure}.
 * 
 * This API is of a higher-level than the experiment procedure API, and is
 * intended to permit straightforward non-destructive editing of an experiment.
 * This motivates a the following design choices when contrasted with the
 * procedure API:
 * 
 * <ul>
 * <li>A "design" object more directly reflects the underlying hierarchical
 * structure of an experiment than a "procedure".</li>
 * <li>Various "wither" methods are provided for functional updates.</li>
 * <li>A "design" object is permitted to be in an invalid state during
 * construction and editing. Exceptions are only thrown when
 * {@link #implementProcedure(Environment) implementing a procedure} for a
 * design.</li>
 * <li>For convenience, a {@link #sharedMethods() shared method} feature is
 * included to support reuse of common elements of an experiment.</li>
 * </ul>
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentDesign extends ExperimentDesignUnit<Absolute, ExperimentDesign> {
  private final SharedMethods sharedMethods;

  private ExperimentDesign(
      ExperimentId id,
      List<ExperimentStepDesign> steps,
      Map<ExperimentId, ExperimentStepDesign> dependents,
      SharedMethods sharedMethods) {
    super(id, steps, dependents);
    this.sharedMethods = sharedMethods;
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj))
      return false;

    var that = (ExperimentDesign) obj;

    return Objects.equals(this.sharedMethods, that.sharedMethods);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sharedMethods, super.hashCode());
  }

  public static ExperimentDesign define(ExperimentId id) {
    return new ExperimentDesign(id, List.of(), Map.of(), SharedMethods.define());
  }

  public ExperimentDesign withId(ExperimentId id) {
    return new ExperimentDesign(id, getSteps(), getDependents(), sharedMethods);
  }

  @Override
  ExperimentDesign with(
      ExperimentId id,
      List<ExperimentStepDesign> steps,
      Map<ExperimentId, ExperimentStepDesign> dependents) {
    return new ExperimentDesign(id, steps, dependents, sharedMethods);
  }

  public ExperimentDesign withSharedMethods(SharedMethods sharedMethods) {
    return new ExperimentDesign(id(), getSteps(), getDependents(), sharedMethods);
  }

  public SharedMethods sharedMethods() {
    return sharedMethods;
  }

  /**
   * For each step of this experiment, substitute any
   * {@link ExperimentStepDesign#isMethodInstance() method instances} with the
   * shared method of the {@link ExperimentStepDesign#sharedMethodId() given id}.
   * 
   * Any variables defined in both the shared method and the instance are
   * overridden by those defined in the instance.
   * 
   * Any substeps defined in both the shared method and the instance are
   * overridden by those defined in the instance. It is an error if any such
   * substeps appear in a different order in the instance to that in which they
   * appear in the shared method.
   * 
   * Substeps are merged in this manner recursively.
   */
  public ExperimentDesign substituteSharedMethods() throws MethodInstanceException {
    return withSubsteps(s -> s.map(t -> t.substituteSharedMethods(ExperimentPath.toRoot(), sharedMethods)));
  }

  /**
   * Try to implement an experiment procedure according to this design. Invocation
   * if this method may fail with an exception for any of the following reasons:
   * <ul>
   * <li>{@link ExecutionPlan#EXECUTE executing} Any shared method instances
   * cannot be {@link #substituteSharedMethods() substituted}.</li>
   * <li>Any steps are missing a defined {@link ExperimentStepDesign#executor()
   * executor}, post shared method substitution.</li>
   * <li>The designed procedure is not valid.</li>
   * </ul>
   * 
   * Most of the thrown exceptions will report the path at which the failure
   * occurred, which allows users/tooling to {@link ExecutionPlan#WITHHOLD
   * withhold} the problem steps and retry. This permits the automatic generation
   * of best-effort procedures, and the reporting of multiple concurrent errors.
   * 
   * @param environment
   * @return a procedure following this design, omitting
   *         {@link ExecutionPlan#WITHHOLD withheld steps}.
   * @throws MethodInstanceException
   * @throws ProcedureException
   * @throws ExperimentStepDesignException
   */
  public Procedure implementProcedure(Environment environment)
      throws MethodInstanceException,
      ProcedureException,
      ExperimentStepDesignException {
    return implementSubstepInstructions(Procedure.empty(id(), environment), ExperimentPath.toRoot(), sharedMethods);
  }
}
