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
package uk.co.saiman.maldi.sampleplate;

import java.util.Optional;

import uk.co.saiman.experiment.sampleplate.SamplePreparation;

// TODO record & value type
public class MaldiSamplePreparation extends SamplePreparation {
  private final Integer barcode;

  public MaldiSamplePreparation(String id, MaldiSamplePlate plate, Integer barcode) {
    super(id, plate);
    this.barcode = barcode;
  }

  public MaldiSamplePreparation(String id, MaldiSamplePlate plate, int barcode) {
    this(id, plate, (Integer) barcode);
  }

  public MaldiSamplePreparation(String id, MaldiSamplePlate plate) {
    this(id, plate, (Integer) null);
  }

  @Override
  public MaldiSamplePlate plate() {
    return (MaldiSamplePlate) super.plate();
  }

  public Optional<Integer> barcode() {
    return Optional.ofNullable(barcode);
  }
}
