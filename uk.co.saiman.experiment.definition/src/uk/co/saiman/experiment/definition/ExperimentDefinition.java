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
package uk.co.saiman.experiment.definition;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.procedure.Procedure;

public class ExperimentDefinition extends Definition<Absolute, ExperimentDefinition> {
  private final ExperimentId id;
  private Procedure procedure;

  /*
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * TODO some notion of "snippets", steps which exist in a shared area and of
   * which instances can be included at multiple locations in the experiment. So
   * for example we could define a shared spectrum processing snippet and attach
   * it as a child of a sequence of spectrum acquisition steps. This way we only
   * have to change the processing setup in one place and it applies to all
   * acquired spectra.
   * 
   * 
   * TODO Justification: this is just a convenience feature. The
   * instruction/executor procedure/conductor model of performing an experiment
   * and collecting results should not be burdened with the complexity of this.
   * 
   * But this class (the experiment definition) is a layer of abstraction above
   * the instruction/procedure model, and is perhaps the appropriate place for
   * such a feature.
   * 
   * "shared definition", "snippet", "method", "fragment", "placeholder",
   * "snippet instance", "method instance"
   * 
   * TODO can we have local overrides of e.g. variables/substeps for snippet
   * instances? If so, how do we keep these in sync if the snippet is updated? An
   * alternative to local overrides in snippet instances, we can have overrides in
   * snippet definitions and allow then to extend one another.
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * TODO perhaps this could be generalised to other ways of generating
   * substeps/instructions?
   * 
   * TODO ... generalise to something equivalent to a pre-processor or macro
   * expander?
   * 
   * TODO ALTERNATIVE: batch processing node, which depends on the results of
   * multiple other nodes and applies processing to all of them.
   * 
   * 
   * 
   */

  private ExperimentDefinition(
      ExperimentId id,
      List<StepDefinition> steps,
      Map<ExperimentId, StepDefinition> dependents) {
    super(steps, dependents);
    this.id = id;
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj))
      return false;

    ExperimentDefinition that = (ExperimentDefinition) obj;

    return Objects.equals(this.id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, super.hashCode());
  }

  public static ExperimentDefinition define(ExperimentId id) {
    return new ExperimentDefinition(id, List.of(), Map.of());
  }

  public ExperimentId id() {
    return id;
  }

  public ExperimentDefinition withId(ExperimentId id) {
    return new ExperimentDefinition(id, getSteps(), getDependents());
  }

  @Override
  ExperimentDefinition with(List<StepDefinition> steps, Map<ExperimentId, StepDefinition> dependents) {
    return new ExperimentDefinition(id, steps, dependents);
  }

  public Procedure procedure(Environment environment) {
    if (procedure == null) {
      procedure = substepsClosure(Procedure.empty(id, environment), this, ExperimentPath.toRoot());
    }
    return procedure;
  }

  private Procedure substepsClosure(Procedure procedure, Definition<?, ?> steps, ExperimentPath<Absolute> parentPath) {
    return steps.substeps().reduce(procedure, (p, s) -> stepClosure(p, s, parentPath), (a, b) -> {
      throw new AssertionError();
    });
  }

  private Procedure stepClosure(Procedure procedure, StepDefinition step, ExperimentPath<Absolute> path) {
    var p = path.resolve(step.id());
    return step.getPlan() == ExecutionPlan.WITHHOLD ? procedure
        : substepsClosure(procedure.withInstruction(p, step.variableMap(), step.executor()), step, p);
  }
}
