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
 * This file is part of uk.co.saiman.instrument.sampleplate.
 *
 * uk.co.saiman.instrument.sampleplate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.sampleplate is distributed in the hope that it will be useful,
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

public abstract class IndexedSamplePlate<U extends SampleArea> implements SamplePlate {
  private static final MapIndex<String> PLATE_ID = new MapIndex<>("id", Accessor.stringAccessor());

  private Map<String, U> sampleAreas;

  public IndexedSamplePlate(Collection<? extends U> sampleAreas) {
    this.sampleAreas = sampleAreas
        .stream()
        .collect(toMap(SampleArea::id, Function.identity(), (a, b) -> b, LinkedHashMap::new));
  }

  public Stream<U> sampleAreas() {
    return sampleAreas.values().stream();
  }

  @Override
  public boolean containsSampleArea(SampleArea sampleArea) {
    U properArea = sampleArea(sampleArea.id());
    return properArea != null && properArea.equals(sampleArea);
  }

  @Override
  public U sampleArea(StateMap state) {
    return sampleAreas.get(state.get(PLATE_ID));
  }

  public U sampleArea(String id) {
    U properArea = sampleAreas.get(id);
    if (properArea == null) {
      throw new IllegalArgumentException(new NullPointerException());
    }
    return properArea;
  }

  @Override
  public StateMap persistSampleArea(SampleArea sampleArea) {
    U properArea = sampleArea(sampleArea.id());
    if (properArea == null || !properArea.equals(sampleArea)) {
      throw new IllegalArgumentException();
    }
    return StateMap.empty().with(PLATE_ID, sampleArea.id());
  }
}
