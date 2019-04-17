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

import static uk.co.saiman.data.function.processing.Convolution.DomainModification.EXTENDING;
import static uk.co.saiman.state.Accessor.doubleAccessor;
import static uk.co.saiman.state.Accessor.intAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.stream.DoubleStream;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.function.processing.Convolution;
import uk.co.saiman.data.function.processing.Convolution.DomainModification;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;

@Component
public class ConvolutionProcess implements ProcessingStrategy<Convolution> {
  private static final MapIndex<double[]> VECTOR = new MapIndex<>(
      "vector",
      doubleAccessor()
          .toStreamAccessor()
          .map(s -> s.mapToDouble(e -> e).toArray(), a -> DoubleStream.of(a).mapToObj(e -> e)));
  private static final MapIndex<Integer> OFFSET = new MapIndex<>("offset", intAccessor());
  private static final MapIndex<Convolution.DomainModification> DOMAIN_MODIFICATION = new MapIndex<>(
      "extend",
      stringAccessor().map(DomainModification::valueOf, Enum::name));

  @Override
  public Convolution createProcessor() {
    return new Convolution();
  }

  @Override
  public Convolution configureProcessor(StateMap state) {
    return new Convolution(
        state.getOptional(VECTOR).orElse(Convolution.NO_OP),
        state.getOptional(OFFSET).orElse(0),
        state.getOptional(DOMAIN_MODIFICATION).orElse(EXTENDING));
  }

  @Override
  public StateMap deconfigureProcessor(Convolution processor) {
    return StateMap
        .empty()
        .with(VECTOR, processor.getConvolutionVector())
        .with(OFFSET, processor.getConvolutionVectorOffset())
        .with(DOMAIN_MODIFICATION, processor.getDomainModification());
  }

  @Override
  public Class<Convolution> getType() {
    return Convolution.class;
  }
}
