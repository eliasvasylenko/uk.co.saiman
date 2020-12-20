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

import static java.util.Objects.requireNonNull;

import java.util.Optional;

// TODO record & value type
public class SamplePreparation {
  private final String id;
  private final SamplePlate plate;

  public SamplePreparation(String id, SamplePlate plate) {
    this.id = requireNonNull(id);
    this.plate = requireNonNull(plate);
  }

  public SamplePreparation(SamplePlate plate) {
    this.id = null;
    this.plate = requireNonNull(plate);
  }

  public Optional<String> id() {
    return Optional.ofNullable(id);
  }

  public SamplePlate plate() {
    return plate;
  }

  public boolean isCompatible(SamplePreparation s) {
    return (id == null || id.equals(s.id)) && plate.equals(s.plate);
  }
}