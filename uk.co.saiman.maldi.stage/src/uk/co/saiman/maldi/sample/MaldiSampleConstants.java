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
package uk.co.saiman.maldi.sample;

import static uk.co.saiman.state.Accessor.intAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.Optional;

import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePlate;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePlateIndex;
import uk.co.saiman.state.Accessor;
import uk.co.saiman.state.StateMap;

public final class MaldiSampleConstants {
  private MaldiSampleConstants() {}

  public static final String SAMPLE_AREA_EXECUTOR = "uk.co.saiman.maldi.executor.samplearea";

  public static final String SAMPLE_AREA_ID = "uk.co.saiman.maldi.variable.samplewell.id";
  public static final Variable<StateMap> SAMPLE_AREA = new Variable<>(
      SAMPLE_AREA_ID,
      Accessor.mapAccessor());

  public static final String SAMPLE_PLATE_EXECUTOR = "uk.co.saiman.maldi.executor.sampleplate";

  public static final String SAMPLE_PLATE_ID = "uk.co.saiman.maldi.variable.sampleplate.id";
  public static final Variable<MaldiSamplePlate> SAMPLE_PLATE = new Variable<>(
      SAMPLE_PLATE_ID,
      globals -> {
        var plateIndex = globals.provideSharedResource(MaldiSamplePlateIndex.class);
        return stringAccessor()
            .map(id -> plateIndex.getSamplePlate(id).get(), plate -> plateIndex.getId(plate).get());
      });

  public static final String SAMPLE_PLATE_PREPARATION_ID_ID = "uk.co.saiman.maldi.variable.sampleplate.preparationid";
  public static final Variable<String> SAMPLE_PLATE_PREPARATION_ID = new Variable<>(
      SAMPLE_PLATE_PREPARATION_ID_ID,
      stringAccessor());

  public static final String SAMPLE_PLATE_BARCODE_ID = "uk.co.saiman.maldi.variable.sampleplate.barcode";
  public static final Variable<Optional<Integer>> SAMPLE_PLATE_BARCODE = new Variable<>(
      SAMPLE_PLATE_BARCODE_ID,
      intAccessor().toOptionalAccessor());
}
