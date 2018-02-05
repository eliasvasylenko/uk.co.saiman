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
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.utility.Copyable;

public abstract class ProcessorState implements Copyable<ProcessorState> {
  public static final String PROCESSING_KEY = "processing";
  public static final String PROCESSOR_TYPE_KEY = "type";

  private final ProcessorType<?> type;
  private final PersistedState state;

  public ProcessorState(ProcessorType<?> type, PersistedState state) {
    this.type = type;
    this.state = state;

    this.state.forString(PROCESSOR_TYPE_KEY).set(type.getId());
  }

  public PersistedState getPersistedState() {
    return state;
  }

  public ProcessorType<?> getProcessorType() {
    return type;
  }

  public abstract DataProcessor getProcessor();

  @Override
  public ProcessorState copy() {
    return getProcessorType().configure(getPersistedState().copy());
  }
}
