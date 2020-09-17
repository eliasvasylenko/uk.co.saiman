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
 * This file is part of uk.co.saiman.maldi.stage.msapex.
 *
 * uk.co.saiman.maldi.stage.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.stage.msapex;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import uk.co.saiman.maldi.sampleplate.MaldiSampleArea;

public class SampleAreaSelection {
  private static final SampleAreaSelection EMPTY = new SampleAreaSelection(Set.of());

  private final Set<MaldiSampleArea> sampleAreas;

  private SampleAreaSelection(Set<MaldiSampleArea> sampleAreas) {
    this.sampleAreas = sampleAreas;
  }

  public static SampleAreaSelection empty() {
    return EMPTY;
  }

  public Stream<MaldiSampleArea> sampleAreas() {
    return sampleAreas.stream();
  }

  public SampleAreaSelection with(MaldiSampleArea... sampleAreas) {
    return with(asList(sampleAreas));
  }

  public SampleAreaSelection with(Collection<? extends MaldiSampleArea> sampleAreas) {
    var newSampleAreas = new HashSet<>(this.sampleAreas);
    newSampleAreas.addAll(sampleAreas);
    return new SampleAreaSelection(newSampleAreas);
  }

  public SampleAreaSelection without(MaldiSampleArea... sampleAreas) {
    return without(asList(sampleAreas));
  }

  public SampleAreaSelection without(Collection<? extends MaldiSampleArea> sampleAreas) {
    var newSampleAreas = new HashSet<>(this.sampleAreas);
    newSampleAreas.removeAll(sampleAreas);
    return new SampleAreaSelection(newSampleAreas);
  }

  @Override
  public String toString() {
    return sampleAreas.toString();
  }
}
