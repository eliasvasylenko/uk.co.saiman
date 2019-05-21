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

import static uk.co.saiman.state.Accessor.mapAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;

public class ProcessorDeclaration {
  private static final MapIndex<String> PROCESSOR_ID = new MapIndex<>(
      "uk.co.saiman.experiment.processor.id",
      stringAccessor());

  public static final Variable<ProcessorDeclaration> PROCESSOR_VARIABLE = new Variable<>(
      "uk.co.saiman.processor",
      mapAccessor(ProcessorDeclaration::fromState, ProcessorDeclaration::toState));

  private final String id;
  private final StateMap state;

  public ProcessorDeclaration(String id, StateMap state) {
    this.id = id;
    this.state = state;
  }

  public static ProcessorDeclaration fromState(StateMap state) {
    String id = state.get(PROCESSOR_ID);
    return new ProcessorDeclaration(id, state.remove(PROCESSOR_ID));
  }

  public StateMap toState() {
    return state().with(PROCESSOR_ID, id());
  }

  public DataProcessor load(ProcessingService service) {
    ProcessingStrategy<?> process = service.findStrategy(id()).get();

    if (process != null) {
      return process.configureProcessor(state());
    } else {
      return new MissingProcessor(id());
    }
  }

  @SuppressWarnings("unchecked")
  public static ProcessorDeclaration save(ProcessingService service, DataProcessor processor) {
    ProcessingStrategy<?> process = service.findStrategy(processor.getClass()).get();
    return new ProcessorDeclaration(
        processor.getClass().getName(),
        ((ProcessingStrategy<DataProcessor>) process).deconfigureProcessor(processor));
  }

  public String id() {
    return id;
  }

  public StateMap state() {
    return state;
  }
}
