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
 * This file is part of uk.co.saiman.maldi.stage.
 *
 * uk.co.saiman.maldi.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.sample;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.maldi.sample.MaldiSamplePlateExecutor.PLATE_SUBMISSION;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.source.Preparation;
import uk.co.saiman.experiment.dependency.source.Production;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.requirement.AdditionalRequirement;
import uk.co.saiman.experiment.requirement.Requirement;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.VariableDeclaration;
import uk.co.saiman.maldi.sample.MaldiSampleAreaExecutor.MaldiXYStageExecutorConfiguration;
import uk.co.saiman.maldi.stage.SampleAreaHold;
import uk.co.saiman.maldi.stage.SamplePlateSubmission;

@Designate(ocd = MaldiXYStageExecutorConfiguration.class, factory = true)
@Component(configurationPid = MaldiSampleAreaExecutor.CONFIGURATION_PID, configurationPolicy = REQUIRE, service = {
    MaldiSampleAreaExecutor.class,
    Executor.class })
public class MaldiSampleAreaExecutor implements Executor {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Maldi XY Stage Experiment Executor", description = "The experiment executor which manages the positioning of the sample stage")
  public @interface MaldiXYStageExecutorConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.maldi.executor.xystage";

  public static final Preparation<SampleAreaHold> IN_POSITION = new Preparation<>(
      "uk.co.saiman.maldi.executor.inposition");

  public static final Variable<String> SAMPLE_WELL_ID = new Variable<>(
      "uk.co.saiman.maldi.variable.samplewell.id",
      stringAccessor());

  @Override
  public Stream<VariableDeclaration> variables() {
    return Stream.of(SAMPLE_WELL_ID.declareRequired());
  }

  @Override
  public Stream<Production<?>> products() {
    return Stream.of(IN_POSITION);
  }

  @Override
  public Requirement<Condition<? extends SamplePlateSubmission>> mainRequirement() {
    return Requirement.on(PLATE_SUBMISSION);
  }

  @Override
  public Stream<AdditionalRequirement<?>> additionalRequirements() {
    return Stream.empty();
  }

  @Override
  public void execute(ExecutionContext context) {
    var plateSubmission = context.acquireDependency(PLATE_SUBMISSION).value();

    var sampleArea = plateSubmission
        .samplePreparation()
        .plate()
        .sampleArea(context.getVariable(SAMPLE_WELL_ID))
        .get();

    /*
     * TODO we need to include raster operation here. Instantiate a virtual raster
     * device over the stage location? But then how do we present the virtual raster
     * in the UI if it's a transient thing?
     * 
     * A raster executor should produce a result which reports the path taken by the
     * raster. This path may have been manually controlled by user!
     * 
     * TODO control of the raster SHOULD pass through here! As only the person who
     * started the experiment should have the authority to give permission to
     * control the raster during the experiment. The executor should HAND control to
     * the UI.
     */

    plateSubmission.requestAnalysisLocation(sampleArea);
    plateSubmission.awaitRequest(30, SECONDS);
    context.prepareCondition(IN_POSITION, null);
  }
}
