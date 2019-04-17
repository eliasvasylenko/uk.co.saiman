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

import static uk.co.saiman.state.Accessor.intAccessor;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.function.processing.BoxFilter;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;

@Component
public class BoxFilterProcess implements ProcessingStrategy<BoxFilter> {
  private static final MapIndex<Integer> WIDTH = new MapIndex<>("width", intAccessor());

  @Override
  public BoxFilter createProcessor() {
    return new BoxFilter();
  }

  @Override
  public BoxFilter configureProcessor(StateMap state) {
    return new BoxFilter(state.getOptional(WIDTH).orElse(BoxFilter.NO_OP));
  }

  @Override
  public StateMap deconfigureProcessor(BoxFilter processor) {
    return StateMap.empty().with(WIDTH, processor.getWidth());
  }

  @Override
  public Class<BoxFilter> getType() {
    return BoxFilter.class;
  }
}
