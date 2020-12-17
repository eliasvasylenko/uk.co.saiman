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

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import uk.co.saiman.data.function.ArraySampledContinuousFunction;
import uk.co.saiman.data.function.IrregularSampledDomain;
import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.data.function.processing.DataProcessor;

public class SampledSpectrum implements Spectrum {
  private final SampledContinuousFunction<Time, Dimensionless> timeData;
  private SampledContinuousFunction<Mass, Dimensionless> massData;

  private final SpectrumCalibration calibration;
  private final DataProcessor processing;

  public SampledSpectrum(
      SampledContinuousFunction<Time, Dimensionless> timeData,
      SpectrumCalibration calibration,
      DataProcessor processing) {
    this.timeData = timeData;
    this.calibration = calibration.withTimeUnit(timeData.domain().getUnit());
    this.processing = processing;
  }

  @Override
  public SampledContinuousFunction<Time, Dimensionless> getTimeData() {
    return timeData;
  }

  @Override
  public SampledContinuousFunction<Mass, Dimensionless> getMassData() {
    if (massData == null)
      massData = processData();
    return massData;
  }

  protected SampledContinuousFunction<Mass, Dimensionless> processData() {
    double[] massValues = new double[timeData.getDepth()];
    double[] intensityValues = new double[timeData.getDepth()];
    for (int i = 0; i < timeData.getDepth(); i++) {
      massValues[i] = calibration.getMass(timeData.domain().getSample(i));
      intensityValues[i] = timeData.range().getSample(i);
    }
    SampledContinuousFunction<Mass, Dimensionless> massFunction = new ArraySampledContinuousFunction<>(
        new IrregularSampledDomain<>(calibration.getMassUnit(), massValues),
        timeData.range().getUnit(),
        intensityValues);
    return processing.process(massFunction);
  }

  @Override
  public SpectrumCalibration getCalibration() {
    return calibration;
  }

  @Override
  public DataProcessor getProcessing() {
    return processing;
  }

  @Override
  public void close() throws Exception {}
}
