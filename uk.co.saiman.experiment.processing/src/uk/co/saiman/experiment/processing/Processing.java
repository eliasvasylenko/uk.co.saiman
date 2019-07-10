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

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.data.function.processing.DataProcessor.identity;
import static uk.co.saiman.experiment.processing.ProcessingAccess.processingAccessor;
import static uk.co.saiman.experiment.processing.ProcessingService.PROCESSING_SERVICE_RESOURCE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.variables.Variable;

public class Processing {
  public static final Variable<Processing> PROCESSING_VARIABLE = new Variable<>(
      "uk.co.saiman.processing",
      environment -> processingAccessor(environment.getStaticValue(PROCESSING_SERVICE_RESOURCE)));

  public static final Variable<DataProcessor> PROCESSOR_VARIABLE = new Variable<>(
      "uk.co.saiman.processor",
      environment -> ProcessingAccess
          .processorAccessor(environment.getStaticValue(PROCESSING_SERVICE_RESOURCE)));

  private List<DataProcessor> steps;

  public Processing() {
    this.steps = emptyList();
  }

  public Processing(Collection<? extends DataProcessor> processors) {
    this.steps = new ArrayList<>(processors);
  }

  public Stream<DataProcessor> steps() {
    return steps.stream();
  }

  public static Collector<DataProcessor, ?, Processing> toProcessing() {
    return collectingAndThen(toList(), Processing::new);
  }

  public DataProcessor getProcessor() {
    return steps().reduce(identity(), DataProcessor::andThen);
  }

  public Processing withStep(DataProcessor step) {
    List<DataProcessor> steps = new ArrayList<>(this.steps);
    steps.add(step);
    return new Processing(steps);
  }

  public Processing withStep(int index, DataProcessor step) {
    List<DataProcessor> steps = new ArrayList<>(this.steps);
    steps.add(index, step);
    return new Processing(steps);
  }

  public Processing withStep(int index, Function<DataProcessor, DataProcessor> step) {
    List<DataProcessor> steps = new ArrayList<>(this.steps);
    steps.set(index, step.apply(steps.get(index)));
    return new Processing(steps);
  }

  public Processing withoutStep(int index) {
    steps.remove(index);
    return new Processing(steps);
  }
}
