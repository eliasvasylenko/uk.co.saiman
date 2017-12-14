/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.spectrum.
 *
 * uk.co.saiman.experiment.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data.spectrum;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import uk.co.saiman.data.function.ContinuousFunction;
import uk.co.saiman.data.function.SampledContinuousFunction;

public class SampledSpectrum implements Spectrum {
  private final SampledContinuousFunction<Time, Dimensionless> timeData;

  private final SpectrumCalibration calibration;
  private final SpectrumProcessor processing;

  public SampledSpectrum(
      SampledContinuousFunction<Time, Dimensionless> timeData,
      SpectrumCalibration calibration,
      SpectrumProcessor processing) {
    this.timeData = timeData;
    this.calibration = calibration;
    this.processing = processing;
  }

  @Override
  public ContinuousFunction<Time, Dimensionless> getTimeData() {
    return timeData;
  }

  @Override
  public ContinuousFunction<Mass, Dimensionless> getMassData() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SpectrumCalibration getCalibration() {
    return calibration;
  }

  @Override
  public SpectrumProcessor getProcessing() {
    return processing;
  }
}
