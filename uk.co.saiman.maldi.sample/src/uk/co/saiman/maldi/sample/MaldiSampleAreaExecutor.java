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
import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;
import static uk.co.saiman.maldi.stage.MaldiStageConstants.PLATE_SUBMISSION;
import static uk.co.saiman.maldi.stage.MaldiStageConstants.SAMPLE_WELL_ID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.dependency.source.Preparation;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.PlanningContext;
import uk.co.saiman.experiment.variables.VariableCardinality;
import uk.co.saiman.maldi.sample.MaldiSampleAreaExecutor.MaldiSampleAreaExecutorConfiguration;
import uk.co.saiman.maldi.stage.SampleAreaHold;

@Designate(ocd = MaldiSampleAreaExecutorConfiguration.class, factory = true)
@Component(configurationPid = MaldiSampleAreaExecutor.CONFIGURATION_PID, configurationPolicy = OPTIONAL, service = {
    MaldiSampleAreaExecutor.class,
    Executor.class })
public class MaldiSampleAreaExecutor implements Executor {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Maldi Sample Area Experiment Executor", description = "The experiment executor which manages the positioning of the sample stage")
  public @interface MaldiSampleAreaExecutorConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.maldi.executor.xystage";

  public static final Preparation<SampleAreaHold> IN_POSITION = new Preparation<>(
      "uk.co.saiman.maldi.executor.inposition");

  @Override
  public void plan(PlanningContext context) {
    context.declareVariable(SAMPLE_WELL_ID, VariableCardinality.REQUIRED);

    context.declareMainRequirement(PLATE_SUBMISSION);

    context.declareProduct(IN_POSITION);
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

    plateSubmission.requestAnalysis(sampleArea);
    plateSubmission.awaitRequest(30, SECONDS);
    context.prepareCondition(IN_POSITION, null);
  }
}
