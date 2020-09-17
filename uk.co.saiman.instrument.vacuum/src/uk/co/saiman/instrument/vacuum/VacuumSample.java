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
 * This file is part of uk.co.saiman.instrument.vacuum.
 *
 * uk.co.saiman.instrument.vacuum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.vacuum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.vacuum;

import javax.measure.Quantity;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Time;

public class VacuumSample {
  private final Quantity<Pressure> pressureMeasurement;
  private final Quantity<Time> sampleTime;

  public VacuumSample(Quantity<Pressure> pressureMeasurement, Quantity<Time> sampleTime) {
    this.pressureMeasurement = pressureMeasurement;
    this.sampleTime = sampleTime;
  }

  public Quantity<Pressure> getMeasuredPressure() {
    return pressureMeasurement;
  }

  public Quantity<Time> getMeasuredTime() {
    return sampleTime;
  }
}
