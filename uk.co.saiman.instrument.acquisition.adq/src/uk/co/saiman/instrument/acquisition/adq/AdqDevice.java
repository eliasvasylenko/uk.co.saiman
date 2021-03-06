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
package uk.co.saiman.instrument.acquisition.adq;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.saiman.instrument.acquisition.AcquisitionController;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;

public interface AdqDevice extends AcquisitionDevice {
  String getSerialNumber();

  AdqProductId getProductId();

  ProductFamily getProductFamily();

  AdqHardwareInterface getHardwareInterface();

  FirmwareRevision getFirmwareRevisionFpga(FpgaTarget target);

  TriggerMode getTriggerMode();

  TestPatternMode getTestPatternMode();

  int getTestPatternConstant();

  double getGain(boolean relative);

  int getOffset(boolean relative);

  @Override
  AdqControl acquireControl(long timeout, TimeUnit unit)
      throws TimeoutException, InterruptedException;

  public interface AdqControl extends AcquisitionController {
    void setTriggerMode(TriggerMode triggerMode);

    void softwareTrigger();

    void setTestPatternMode(TestPatternMode testPatternMode);

    void setTestPatternConstant(int testPatternConstant);

    void setGainAndOffset(double gain, int offset, boolean relativeToDefault);
  }
}
