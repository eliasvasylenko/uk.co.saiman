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
package uk.co.saiman.maldi.stage.impl;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.state.Accessor.intAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.instruction.ExecutionContext;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.instruction.ExecutorException;
import uk.co.saiman.experiment.instruction.IndirectRequirements;
import uk.co.saiman.experiment.production.Nothing;
import uk.co.saiman.experiment.production.Preparation;
import uk.co.saiman.experiment.production.Production;
import uk.co.saiman.experiment.requirement.Requirement;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.VariableDeclaration;
import uk.co.saiman.maldi.stage.SamplePlate;
import uk.co.saiman.maldi.stage.SamplePlateStage;
import uk.co.saiman.maldi.stage.SamplePlateSubmission;
import uk.co.saiman.maldi.stage.SamplePreparation;
import uk.co.saiman.maldi.stage.impl.MaldiSamplePlateExecutor.MaldiXYStageExecutorConfiguration;
import uk.co.saiman.osgi.ServiceIndex;

@Designate(ocd = MaldiXYStageExecutorConfiguration.class, factory = true)
@Component(configurationPid = MaldiSamplePlateExecutor.CONFIGURATION_PID, configurationPolicy = REQUIRE, service = {
    MaldiSamplePlateExecutor.class,
    Executor.class })
public class MaldiSamplePlateExecutor implements Executor<Nothing> {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Maldi XY Stage Experiment Executor", description = "The experiment executor which manages the positioning of the sample stage")
  public @interface MaldiXYStageExecutorConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.maldi.executor.sampleplate";

  public static final Preparation<SamplePlateSubmission> PLATE_SUBMISSION = new Preparation<>(
      "uk.co.saiman.maldi.executor.platesubmission",
      SamplePlateSubmission.class);

  public static final Variable<String> SAMPLE_PLATE_ID = new Variable<>(
      "uk.co.saiman.maldi.variable.sampleplate.id",
      stringAccessor());

  public static final Variable<UUID> SAMPLE_PLATE_PREPARATION_ID = new Variable<>(
      "uk.co.saiman.maldi.variable.sampleplate.preparationid",
      stringAccessor().map(UUID::fromString, UUID::toString));

  public static final Variable<Integer> SAMPLE_PLATE_BARCODE = new Variable<>(
      "uk.co.saiman.maldi.variable.sampleplate.barcode",
      intAccessor());

  private final SamplePlateStage stageDevice;
  private final ServiceIndex<SamplePlate, String, SamplePlate> plateIndex;

  private SamplePreparation loadedPreparation;

  @Activate
  public MaldiSamplePlateExecutor(
      @Reference(name = "stageDevice") SamplePlateStage stageDevice,
      BundleContext context) {
    this.stageDevice = stageDevice;
    this.plateIndex = ServiceIndex.open(context, SamplePlate.class);
    this.loadedPreparation = null;
  }

  public SamplePlateStage sampleDevice() {
    return stageDevice;
  }

  @Override
  public Stream<VariableDeclaration> variables() {
    return Stream.of(SAMPLE_PLATE_ID.declareRequired(), SAMPLE_PLATE_BARCODE.declareOptional());
  }

  @Override
  public Stream<Production<?>> products() {
    return Stream.of(PLATE_SUBMISSION);
  }

  @Override
  public Requirement<Nothing> directRequirement() {
    return Requirement.none();
  }

  @Override
  public Stream<IndirectRequirements> indirectRequirements() {
    return Stream.empty();
  }

  @Override
  public void execute(ExecutionContext<Nothing> context) {
    var requestedPreparation = new SamplePreparation(
        context.getVariable(SAMPLE_PLATE_ID),
        plateIndex.get(context.getVariable(SAMPLE_PLATE_ID)).get().serviceObject(),
        context.getVariables().get(SAMPLE_PLATE_BARCODE).orElse(null));

    try (var control = sampleDevice().acquireControl(2, SECONDS)) {
      /*
       * TODO when we are *not* executing an experiment, we need to listen for
       * exchanges and null the loadedPlate when they occur.
       */
      if ((loadedPreparation != null
          && !loadedPreparation.id().equals(requestedPreparation.id()))) {
        control.requestExchange();
      } else {
        control.requestReady();
      }
      control.awaitReady(5, MINUTES);
      loadedPreparation = requestedPreparation;

      context
          .prepareCondition(
              PLATE_SUBMISSION,
              new SamplePlateSubmission(control, loadedPreparation));

    } catch (TimeoutException | InterruptedException e) {
      new ExecutorException("Failed to acquire control of stage device", e);
    }
  }
}
