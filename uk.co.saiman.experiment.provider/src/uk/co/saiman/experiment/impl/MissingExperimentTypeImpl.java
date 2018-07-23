/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.impl;

import static java.lang.String.format;

import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.MissingExperimentType;
import uk.co.saiman.experiment.ProcessingContext;
import uk.co.saiman.experiment.persistence.StateMap;

public class MissingExperimentTypeImpl<T> implements MissingExperimentType<T> {
  private final ExperimentProperties text;
  private final String id;

  protected MissingExperimentTypeImpl(ExperimentProperties text, String id) {
    this.text = text;
    this.id = id;
  }

  @Override
  public String getName() {
    return text.missingExperimentType(id).toString();
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public T process(ProcessingContext<StateMap, T> context) {
    throw new ExperimentException(format("Cannot execute missing experiment type %s", id));
  }
}
