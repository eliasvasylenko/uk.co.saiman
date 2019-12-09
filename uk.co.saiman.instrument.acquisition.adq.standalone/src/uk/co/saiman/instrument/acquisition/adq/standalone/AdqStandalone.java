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
 * This file is part of uk.co.saiman.instrument.acquisition.adq.standalone.
 *
 * uk.co.saiman.instrument.acquisition.adq.standalone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.acquisition.adq.standalone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.acquisition.adq.standalone;

import static uk.co.saiman.instrument.acquisition.adq.FpgaTarget.ALG_FPGA;
import static uk.co.saiman.instrument.acquisition.adq.FpgaTarget.COMM_FPGA;
import static uk.co.saiman.instrument.acquisition.adq.TestPatternMode.COUNT_ALTERNATING;
import static uk.co.saiman.instrument.acquisition.adq.TriggerMode.SOFTWARE_TRIGGER;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.saiman.instrument.acquisition.adq.TraceLevel;
import uk.co.saiman.instrument.acquisition.adq.impl.Adq114DeviceImpl;
import uk.co.saiman.instrument.acquisition.adq.impl.AdqDeviceManager;

public class AdqStandalone {
  public static void main(String... args) throws TimeoutException, InterruptedException {
    System.out.println("API revision: " + AdqDeviceManager.getApiRevision());

    try (var deviceManager = new AdqDeviceManager()) {
      deviceManager
          .setLog(
              TraceLevel.INFO,
              Path
                  .of(
                      "/home/eliasv/git/uk.co.saiman/uk.co.saiman.instrument.acquisition.adq.standalone/generated/"));

      var device = new Adq114DeviceImpl(deviceManager);

      System.out.println("Device ID: " + device.getProductId());
      System.out.println("Serial No: " + device.getSerialNumber());
      System.out.println("Product Family: " + device.getProductFamily());
      System.out.println("Firmware ALG: " + device.getFirmwareRevisionFpga(ALG_FPGA));
      System.out.println("Firmware COMM: " + device.getFirmwareRevisionFpga(COMM_FPGA));
      System.out.println("Hardware Interface: " + device.getHardwareInterface());

      try (var controller = device.acquireControl(0, TimeUnit.SECONDS)) {
        controller.setTriggerMode(SOFTWARE_TRIGGER);
        controller.setTestPatternMode(COUNT_ALTERNATING);

        System.out.println("Trigger Mode: " + device.getTriggerMode());
        System.out.println("Test Pattern Mode: " + device.getTestPatternMode());

        controller.startAcquisition();
      }
    }
  }
}
