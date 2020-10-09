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
 * This file is part of uk.co.saiman.experiment.osgi.
 *
 * uk.co.saiman.experiment.osgi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.osgi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.shell;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.shell.converters.ShellProperties.COMMAND_FUNCTION_KEY;
import static uk.co.saiman.shell.converters.ShellProperties.COMMAND_SCOPE_PROPERTY;

import java.io.IOException;
import java.util.List;

import org.apache.felix.service.command.Descriptor;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.design.ExperimentDesign;
import uk.co.saiman.experiment.design.ExperimentStepDesign;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.service.ExecutorService;

/**
 * Provide commands to the GoGo shell for interacting with experiments.
 * 
 * @author Elias N Vasylenko
 */
@Component(immediate = true, service = ExperimentCommands.class, property = { COMMAND_SCOPE_PROPERTY,
    COMMAND_FUNCTION_KEY + "=defineExperiment", COMMAND_FUNCTION_KEY + "=defineStep",
    COMMAND_FUNCTION_KEY + "=listExecutors" })
public class ExperimentCommands {
  private final ExecutorService executors;

  @Activate
  public ExperimentCommands(@Reference ExecutorService executors) {
    this.executors = executors;
  }

  public static final String EXPERIMENT_ID = "the ID of the experiment";

  public static final String DEFINE_EXPERIMENT = "define an empty experiment";

  @Descriptor(DEFINE_EXPERIMENT)
  public ExperimentDesign defineExperiment(@Descriptor(EXPERIMENT_ID) ExperimentId id) {
    return ExperimentDesign.define(id);
  }

  public static final String STEP_ID = "the ID of the experiment step";

  public static final String STEP_EXECUTOR = "the executor of the experiment step";

  public static final String DEFINE_STEP = "define an empty step";

  @Descriptor(DEFINE_STEP)
  public ExperimentStepDesign defineStep(@Descriptor(STEP_ID) ExperimentId id, @Descriptor(STEP_EXECUTOR) Executor executor)
      throws IOException {
    return ExperimentStepDesign.define(id, executor);
  }

  public static final String LIST_EXECUTORS = "list available experiment step executors";

  @Descriptor(DEFINE_STEP)
  public List<Executor> listExecutors() {
    return executors.executors().collect(toList());
  }
}
