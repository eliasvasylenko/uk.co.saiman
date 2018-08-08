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
 * This file is part of uk.co.saiman.experiment.processing.
 *
 * uk.co.saiman.experiment.processing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.processing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.processing;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.state.StateMap;

public class MissingProcessorType implements Processor<MissingProcessorType> {
  private final String id;
  private final ProcessingProperties text;
  private final StateMap state;

  public MissingProcessorType(String id, ProcessingProperties text) {
    this(id, text, StateMap.empty());
  }

  public MissingProcessorType(String id, ProcessingProperties text, StateMap state) {
    this.id = id;
    this.text = text;
    this.state = state;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return text.missingProcessor().get();
  }

  @Override
  public StateMap getState() {
    return state;
  }

  @Override
  public MissingProcessorType withState(StateMap state) {
    return new MissingProcessorType(id, text, state);
  }

  @Override
  public DataProcessor getProcessor() {
    throw new ExperimentException("Cannot find processor " + id);
  }

  @Override
  public Class<MissingProcessorType> getType() {
    return MissingProcessorType.class;
  }
}
