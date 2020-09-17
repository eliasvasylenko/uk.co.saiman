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
 * This file is part of uk.co.saiman.data.
 *
 * uk.co.saiman.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data.spectrum;

import java.util.function.Function;

import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

public interface SpectrumCalibration {
  Unit<Time> getTimeUnit();

  Unit<Mass> getMassUnit();

  double getMass(double time);

  static SpectrumCalibration withUnits(
      Unit<Time> time,
      Unit<Mass> mass,
      Function<Double, Double> function) {
    return new SpectrumCalibration() {
      @Override
      public Unit<Time> getTimeUnit() {
        return time;
      }

      @Override
      public Unit<Mass> getMassUnit() {
        return mass;
      }

      @Override
      public double getMass(double time) {
        return function.apply(time);
      }
    };
  }

  default SpectrumCalibration withTimeUnit(Unit<Time> time) {
    SpectrumCalibration baseCalibration = this;
    UnitConverter converter = time.getConverterTo(baseCalibration.getTimeUnit());
    return new SpectrumCalibration() {
      @Override
      public Unit<Time> getTimeUnit() {
        return time;
      }

      @Override
      public Unit<Mass> getMassUnit() {
        return baseCalibration.getMassUnit();
      }

      @Override
      public double getMass(double time) {
        return baseCalibration.getMass(converter.convert(time));
      }
    };
  }

  default SpectrumCalibration withMassUnit(Unit<Mass> mass) {
    SpectrumCalibration baseCalibration = this;
    UnitConverter converter = baseCalibration.getMassUnit().getConverterTo(mass);
    return new SpectrumCalibration() {
      @Override
      public Unit<Time> getTimeUnit() {
        return baseCalibration.getTimeUnit();
      }

      @Override
      public Unit<Mass> getMassUnit() {
        return mass;
      }

      @Override
      public double getMass(double time) {
        return converter.convert(baseCalibration.getMass(time));
      }
    };
  }
}
