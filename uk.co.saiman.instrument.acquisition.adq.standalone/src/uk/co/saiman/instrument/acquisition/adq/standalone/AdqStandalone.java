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
