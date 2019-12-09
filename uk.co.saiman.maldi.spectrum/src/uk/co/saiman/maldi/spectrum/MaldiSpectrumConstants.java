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
 * This file is part of uk.co.saiman.maldi.spectrum.
 *
 * uk.co.saiman.maldi.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.spectrum;

import static uk.co.saiman.measurement.Quantities.quantityFormat;
import static uk.co.saiman.measurement.Units.dalton;
import static uk.co.saiman.state.Accessor.intAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

import uk.co.saiman.experiment.variables.Variable;

public final class MaldiSpectrumConstants {
  private MaldiSpectrumConstants() {}

  public static final String SPECTRUM_EXECUTOR = "uk.co.saiman.maldi.executor.spectrum";

  public static final String SPECTRUM_MASS_LIMIT_ID = "uk.co.saiman.maldi.variable.spectrum.masslimit";
  public static final Variable<Quantity<Mass>> SPECTRUM_MASS_LIMIT = new Variable<>(
      SPECTRUM_MASS_LIMIT_ID,
      stringAccessor()
          .map(
              string -> quantityFormat().parse(string).asType(Mass.class).to(dalton().getUnit()),
              quantityFormat()::format));

  public static final String SPECTRUM_ACQUISITION_COUNT_ID = "uk.co.saiman.maldi.variable.spectrum.acquisitioncount";
  public static final Variable<Integer> SPECTRUM_ACQUISITION_COUNT = new Variable<>(
      SPECTRUM_ACQUISITION_COUNT_ID,
      intAccessor());
}
