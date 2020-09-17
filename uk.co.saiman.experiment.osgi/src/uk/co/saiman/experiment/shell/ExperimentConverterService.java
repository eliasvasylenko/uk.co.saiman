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
 * This file is part of uk.co.saiman.shell.
 *
 * uk.co.saiman.shell is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.shell is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.shell;

import org.apache.felix.service.command.Converter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.format.Payload;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.definition.ExperimentDefinition;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.definition.json.JsonExperimentDefinitionFormat;
import uk.co.saiman.experiment.definition.json.JsonStepDefinitionFormat;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.service.ExecutorService;

@Component
public class ExperimentConverterService implements Converter {
  private final JsonExperimentDefinitionFormat experimentDefinitionFormat;
  private final JsonStepDefinitionFormat stepDefinitionFormat;

  private final ExecutorService executors;

  @Activate
  public ExperimentConverterService(@Reference ExecutorService executors) {
    this.stepDefinitionFormat = new JsonStepDefinitionFormat(executors);
    this.experimentDefinitionFormat = new JsonExperimentDefinitionFormat(stepDefinitionFormat);
    this.executors = executors;
  }

  @Override
  public Object convert(Class<?> type, Object object) {
    if (!(object instanceof CharSequence)) {
      return null;
    }

    if (type.isAssignableFrom(ExperimentId.class)) {
      return ExperimentId.fromName(object.toString());

    } else if (type.isAssignableFrom(ExperimentPath.class)) {
      return ExperimentPath.fromString(object.toString());

    } else if (type.isAssignableFrom(Executor.class)) {
      return executors.getExecutor(object.toString());

    } else if (type.isAssignableFrom(ExperimentDefinition.class)) {
      return experimentDefinitionFormat.decodeString(object.toString()).data;

    } else if (type.isAssignableFrom(StepDefinition.class)) {
      return stepDefinitionFormat.decodeString(object.toString()).data;
    }

    return null;
  }

  @Override
  public String format(Object object, int detail, Converter converter) {
    if (object instanceof ExperimentId) {
      return ((ExperimentId) object).name();

    } else if (object instanceof ExperimentPath<?>) {
      return ((ExperimentPath<?>) object).toString();

    } else if (object instanceof Executor) {
      switch (detail) {
      case INSPECT:
        return executors.getId((Executor) object);
      default:
        return executors.getId((Executor) object);
      }

    } else if (object instanceof ExperimentDefinition) {
      var definition = (ExperimentDefinition) object;
      switch (detail) {
      case INSPECT:
        return experimentDefinitionFormat.encodeString(new Payload<>(definition));
      default:
        return definition.id().toString();
      }

    } else if (object instanceof StepDefinition) {
      var definition = (StepDefinition) object;
      switch (detail) {
      case INSPECT:
        return stepDefinitionFormat.encodeString(new Payload<>(definition));
      default:
        return definition.id().toString();
      }
    }

    return null;
  }
}
