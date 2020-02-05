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
 * This file is part of uk.co.saiman.instrument.acquisition.adq.
 *
 * uk.co.saiman.instrument.acquisition.adq is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.acquisition.adq is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.acquisition.adq.impl;

import static uk.co.saiman.measurement.Units.count;
import static uk.co.saiman.measurement.Units.hertz;
import static uk.co.saiman.measurement.Units.second;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;

import com.sun.jna.Pointer;

import uk.co.saiman.instrument.acquisition.adq.Adq114DataFormat;
import uk.co.saiman.instrument.acquisition.adq.Adq114Device;
import uk.co.saiman.instrument.acquisition.adq.Adq114Device.Adq114Control;
import uk.co.saiman.instrument.acquisition.adq.impl.AdqDeviceManager.AdqLib;
import uk.co.saiman.log.Log;
import uk.co.saiman.measurement.scalar.Scalar;

public class Adq114DeviceImpl extends AdqDeviceImpl<Adq114Control> implements Adq114Device {
  private static final Quantity<Frequency> REFERENCE_FREQUENCY = new Scalar<>(hertz().mega(), 10);
  private static final int PLL_MULTIPLIER = 160;
  private static final int DEFAULT_SAMPLE_DEPTH = 512 * 32;
  private static final Adq114DataFormat DATA_FORMAT = Adq114DataFormat.UNPACKED_32BIT;

  private int pllDivider = 2;
  private int accumulations = 1;

  public Adq114DeviceImpl(AdqDeviceManager manager, Log log) {
    this(manager, null, log);
  }

  public Adq114DeviceImpl(AdqDeviceManager manager, String serialNumber, Log log) {
    super(manager, serialNumber, log);

    setSampleDepth(DEFAULT_SAMPLE_DEPTH);
  }

  @Override
  public Quantity<Frequency> getSampleFrequency() {
    return REFERENCE_FREQUENCY.multiply(PLL_MULTIPLIER).divide(pllDivider);
  }

  @Override
  public int getPllDivider() {
    return pllDivider;
  }

  @Override
  public int getAccumulationsPerAcquisition() {
    return accumulations;
  }

  @Override
  public Adq114DataFormat getDataFormat() {
    return DATA_FORMAT;
  }

  @Override
  public Unit<Dimensionless> getSampleIntensityUnit() {
    return count().getUnit();
  }

  @Override
  public Unit<Time> getSampleTimeUnit() {
    return second().micro().getUnit();
  }

  @Override
  protected Adq114Control createController(ControlContext context) {
    return new Adq114ControlImpl(context);
  }

  public class Adq114ControlImpl extends AdqControlImpl implements Adq114Control {
    public Adq114ControlImpl(ControlContext context) {
      super(context);
    }

    @Override
    public void setPllDivider(int pllDivider) {
      if (pllDivider < 2 || pllDivider > 20) {
        throw new IndexOutOfBoundsException(pllDivider);
      }
      Adq114DeviceImpl.this.pllDivider = pllDivider;
    }

    @Override
    public void setAccumulationsPerAcquisition(int accumulations) {
      Adq114DeviceImpl.this.accumulations = accumulations;
    }

    @Override
    protected void startAcquisition(AdqLib lib, Pointer controlUnit, int deviceNumber) {
      try (var acquisition = new MultiRecordAcquisition(
          Adq114DeviceImpl.this,
          lib,
          controlUnit,
          deviceNumber,
          getLog())) {
        acquisition.acquire();
      }
    }
  }
}
