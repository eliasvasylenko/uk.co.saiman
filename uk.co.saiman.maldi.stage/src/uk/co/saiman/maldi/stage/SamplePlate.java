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
 * This file is part of uk.co.saiman.maldi.stage.
 *
 * uk.co.saiman.maldi.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.stage;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static uk.co.saiman.collection.StreamUtilities.throwingMerger;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.measure.quantity.Length;

import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class SamplePlate {
  private final Map<String, SampleArea> sampleLocations;
  private final XYCoordinate<Length> barcodeLocation;

  public SamplePlate(
      Collection<? extends SampleArea> sampleLocations,
      XYCoordinate<Length> barcodeLocation) {
    this.sampleLocations = mapSampleLocations(sampleLocations);
    this.barcodeLocation = requireNonNull(barcodeLocation);
  }

  public SamplePlate(Collection<? extends SampleArea> sampleLocations) {
    this.sampleLocations = mapSampleLocations(sampleLocations);
    this.barcodeLocation = null;
  }

  private static Map<String, SampleArea> mapSampleLocations(
      Collection<? extends SampleArea> sampleLocations) {
    return requireNonNull(sampleLocations)
        .stream()
        .collect(toMap(SampleArea::id, identity(), throwingMerger(), LinkedHashMap::new));
  }

  public Stream<SampleArea> sampleAreas() {
    return sampleLocations.values().stream();
  }

  public Optional<SampleArea> sampleArea(String id) {
    return Optional.ofNullable(sampleLocations.get(id));
  }

  public Optional<XYCoordinate<Length>> barcodeLocation() {
    return Optional.ofNullable(barcodeLocation);
  }
}
