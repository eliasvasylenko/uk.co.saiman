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
 * This file is part of uk.co.saiman.experiment.sampleplate.
 *
 * uk.co.saiman.experiment.sampleplate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.sampleplate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.sampleplate;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.state.Accessor;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;

public abstract class IndexedSamplePlate<T extends SampleArea> implements SamplePlate {
  private static final MapIndex<String> PLATE_ID = new MapIndex<>("id", Accessor.stringAccessor());

  private Map<String, T> sampleAreas;

  public IndexedSamplePlate(Collection<? extends T> sampleAreas) {
    this.sampleAreas = sampleAreas
        .stream()
        .collect(toMap(SampleArea::id, Function.identity(), (a, b) -> b, LinkedHashMap::new));
  }

  public Stream<T> sampleAreas() {
    return sampleAreas.values().stream();
  }

  @Override
  public T sampleArea(StateMap state) {
    return sampleAreas.get(state.get(PLATE_ID));
  }

  public T sampleArea(String id) {
    var properArea = sampleAreas.get(id);
    if (properArea == null) {
      throw new IllegalArgumentException(new NullPointerException());
    }
    return properArea;
  }

  @Override
  public StateMap persistSampleArea(SampleArea sampleArea) {
    var properArea = sampleArea(sampleArea.id());
    if (!properArea.equals(sampleArea)) {
      throw new IllegalArgumentException();
    }
    return StateMap.empty().with(PLATE_ID, sampleArea.id());
  }
}
