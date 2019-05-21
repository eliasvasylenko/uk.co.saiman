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

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.processing.Processing.toProcessing;
import static uk.co.saiman.state.Accessor.listAccessor;
import static uk.co.saiman.state.StateList.toStateList;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.state.State;
import uk.co.saiman.state.StateList;

public class ProcessingDeclaration {
  public static final Variable<ProcessingDeclaration> PROCESSING_VARIABLE = new Variable<>(
      "uk.co.saiman.processing",
      listAccessor(ProcessingDeclaration::fromState, ProcessingDeclaration::toState));

  private final List<ProcessorDeclaration> processors;

  public ProcessingDeclaration(Collection<? extends ProcessorDeclaration> processors) {
    this.processors = List.copyOf(processors);
  }

  public static ProcessingDeclaration fromState(StateList state) {
    return state
        .stream()
        .map(State::asMap)
        .map(ProcessorDeclaration::fromState)
        .collect(toProcessingDeclaration());
  }

  public StateList toState() {
    return processors().map(ProcessorDeclaration::toState).collect(toStateList());
  }

  public Processing load(ProcessingService service) {
    return processors().map(p -> p.load(service)).collect(toProcessing());
  }

  public static ProcessingDeclaration save(ProcessingService service, Processing processing) {
    return processing
        .steps()
        .map(processor -> ProcessorDeclaration.save(service, processor))
        .collect(toProcessingDeclaration());
  }

  public Stream<ProcessorDeclaration> processors() {
    return processors.stream();
  }

  public static Collector<ProcessorDeclaration, ?, ProcessingDeclaration> toProcessingDeclaration() {
    return collectingAndThen(toList(), ProcessingDeclaration::new);
  }
}
