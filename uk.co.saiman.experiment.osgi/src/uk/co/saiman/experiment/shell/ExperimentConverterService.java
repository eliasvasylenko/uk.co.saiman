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

import org.apache.felix.service.command.Converter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.format.Payload;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.design.ExperimentDesign;
import uk.co.saiman.experiment.design.ExperimentStepDesign;
import uk.co.saiman.experiment.design.json.JsonExperimentDesignFormat;
import uk.co.saiman.experiment.design.json.JsonStepDesignFormat;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.service.ExecutorService;

@Component
public class ExperimentConverterService implements Converter {
  private final JsonExperimentDesignFormat experimentDefinitionFormat;
  private final JsonStepDesignFormat stepDefinitionFormat;

  private final ExecutorService executors;

  @Activate
  public ExperimentConverterService(@Reference ExecutorService executors) {
    this.stepDefinitionFormat = new JsonStepDesignFormat(executors);
    this.experimentDefinitionFormat = new JsonExperimentDesignFormat(stepDefinitionFormat);
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

    } else if (type.isAssignableFrom(ExperimentDesign.class)) {
      return experimentDefinitionFormat.decodeString(object.toString()).data;

    } else if (type.isAssignableFrom(ExperimentStepDesign.class)) {
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

    } else if (object instanceof ExperimentDesign) {
      var definition = (ExperimentDesign) object;
      switch (detail) {
      case INSPECT:
        return experimentDefinitionFormat.encodeString(new Payload<>(definition));
      default:
        return definition.id().toString();
      }

    } else if (object instanceof ExperimentStepDesign) {
      var definition = (ExperimentStepDesign) object;
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
