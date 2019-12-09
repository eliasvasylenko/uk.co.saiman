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

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

// TODO record & value type
public class SamplePreparation {
  private final UUID id;
  private final SamplePlate plate;
  private final Map<String, SampleArea> areas;

  public SamplePreparation(UUID id, SamplePlate plate) {
    this.id = requireNonNull(id);
    this.plate = requireNonNull(plate);
    this.areas = new HashMap<>();
  }

  public SamplePreparation(SamplePlate plate) {
    this(UUID.randomUUID(), plate);
  }

  public UUID id() {
    return id;
  }

  public SamplePlate plate() {
    return plate;
  }

  public Stream<? extends SampleArea> sampleAreas() {
    return areas.values().stream();
  }

  public Optional<? extends SampleArea> sampleArea(String id) {
    return Optional.of(areas.get(id));
  }
}
