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
 * This file is part of uk.co.saiman.messaging.
 *
 * uk.co.saiman.messaging is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.messaging is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.commands;

import static uk.co.saiman.shell.converters.ShellProperties.COMMAND_FUNCTION_KEY;
import static uk.co.saiman.shell.converters.ShellProperties.COMMAND_SCOPE_PROPERTY;

import java.io.IOException;

import org.apache.felix.service.command.Descriptor;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.definition.ExperimentDefinition;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.executor.Executor;

/**
 * Provide commands to the GoGo shell for interacting with experiments.
 * 
 * @author Elias N Vasylenko
 */
@Component(immediate = true, service = ExperimentCommands.class, property = { COMMAND_SCOPE_PROPERTY,
    COMMAND_FUNCTION_KEY + "=defineExperiment", COMMAND_FUNCTION_KEY + "=defineStep" })
public class ExperimentCommands {
  public static final String EXPERIMENT_ID = "the ID of the experiment";

  public static final String DEFINE_EXPERIMENT = "open the given channel for reading data";

  /**
   * Command: {@value #DEFINE_EXPERIMENT}
   * 
   * @param id {@value #EXPERIMENT_ID}
   * @throws IOException problem opening the channel
   */
  @Descriptor(DEFINE_EXPERIMENT)
  public ExperimentDefinition defineExperiment(@Descriptor(EXPERIMENT_ID) ExperimentId id) {
    return ExperimentDefinition.define(id);
  }

  public static final String STEP_ID = "the ID of the experiment step";

  public static final String STEP_EXECUTOR = "the executor of the experiment step";

  public static final String DEFINE_STEP = "open the given channel for reading messages";

  /**
   * Command: {@value #DEFINE_STEP}
   * 
   * @param id       {@value #STEP_ID}
   * @param executor {@value #STEP_EXECUTOR}
   * @throws IOException problem opening the channel
   */
  @Descriptor(DEFINE_STEP)
  public StepDefinition defineStep(@Descriptor(STEP_ID) ExperimentId id, @Descriptor(STEP_EXECUTOR) Executor executor)
      throws IOException {
    return StepDefinition.define(id, executor);
  }
}
