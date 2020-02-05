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

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;
import static uk.co.saiman.experiment.osgi.ExperimentServiceConstants.EXECUTOR_ID;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_PLATE;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_PLATE_BARCODE;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_PLATE_EXECUTOR;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_PLATE_PREPARATION_ID;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.PlanningContext;
import uk.co.saiman.experiment.variables.VariableCardinality;
import uk.co.saiman.maldi.sample.MaldiSamplePlateExecutor.MaldiXYStageExecutorConfiguration;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePreparation;
import uk.co.saiman.maldi.stage.MaldiStageController;

@Designate(ocd = MaldiXYStageExecutorConfiguration.class, factory = true)
@Component(
    configurationPid = MaldiSamplePlateExecutor.CONFIGURATION_PID,
    configurationPolicy = OPTIONAL,
    service = { MaldiSamplePlateExecutor.class, Executor.class },
    property = EXECUTOR_ID + "=" + SAMPLE_PLATE_EXECUTOR)
public class MaldiSamplePlateExecutor implements Executor {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Maldi Sample Plate Experiment Executor",
      description = "The experiment executor which manages the positioning of the sample stage")
  public @interface MaldiXYStageExecutorConfiguration {}

  static final String CONFIGURATION_PID = SAMPLE_PLATE_EXECUTOR + ".impl";

  private MaldiSamplePreparation loadedPreparation;

  @Activate
  public MaldiSamplePlateExecutor(BundleContext context) {
    this.loadedPreparation = null;
  }

  @Override
  public void plan(PlanningContext context) {
    context.declareVariable(SAMPLE_PLATE_PREPARATION_ID, VariableCardinality.REQUIRED);
    context.declareVariable(SAMPLE_PLATE, VariableCardinality.REQUIRED);
    context.declareVariable(SAMPLE_PLATE_BARCODE, VariableCardinality.OPTIONAL);

    context.declareResourceRequirement(MaldiStageController.class);

    context.preparesCondition(SamplePlateSubmission.class);
  }

  @Override
  public void execute(ExecutionContext context) {
    System.out.println("get variables ...");
    var requestedPreparation = new MaldiSamplePreparation(
        context.getVariable(SAMPLE_PLATE_PREPARATION_ID),
        context.getVariable(SAMPLE_PLATE),
        context.getVariables().get(SAMPLE_PLATE_BARCODE).flatMap(b -> b).orElse(null));
    System.out.println("got!");

    System.out.println("acquire resource ...");
    var control = context.acquireResource(MaldiStageController.class).value();
    System.out.println("acquired!");

    System.out.println("submit request ...");
    /*
     * TODO when we are *not* executing an experiment, we need to listen for
     * exchanges and null the loadedPlate when they occur.
     */
    if (loadedPreparation == null || loadedPreparation.id().equals(requestedPreparation.id())
        || (loadedPreparation.plate().equals(requestedPreparation.plate()) && loadedPreparation
            .barcode()
            .flatMap(l -> requestedPreparation.barcode().filter(l::equals))
            .isPresent())) {
      control.requestReady();
    } else {
      control.requestExchange();
    }
    System.out.println("requested!");

    /*
     * Enough time for an exchange. If none is needed the controller should
     * internally have a shorter time out to detect motor failure.
     */
    System.out.println("await ready ...");
    control.awaitReady(10, MINUTES);
    System.out.println("ready!");
    loadedPreparation = requestedPreparation;

    context
        .prepareCondition(
            SamplePlateSubmission.class,
            new SamplePlateSubmission(control, loadedPreparation));

  }
}
