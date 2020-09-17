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

import static uk.co.saiman.instrument.acquisition.adq.TestPatternMode.NORMAL_OPERATION;
import static uk.co.saiman.instrument.acquisition.adq.TriggerMode.SOFTWARE;
import static uk.co.saiman.measurement.Units.count;
import static uk.co.saiman.measurement.Units.second;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uk.co.saiman.data.spectrum.ContinuousFunctionAccumulator;
import uk.co.saiman.instrument.acquisition.adq.impl.Adq114DeviceImpl;
import uk.co.saiman.instrument.acquisition.adq.impl.AdqDeviceManager;
import uk.co.saiman.log.Log;
import uk.co.saiman.msapex.chart.ContinuousFunctionChart;
import uk.co.saiman.msapex.chart.MetricTickUnits;
import uk.co.saiman.msapex.chart.QuantityAxis;

public class AdqStandalone extends Application {
  private static final Log LOG = Log
      .simpleLog((level, message) -> System.out.println(level + ": " + message));

  public static void main(String... args) throws TimeoutException, InterruptedException {
    launch(args);
  }

  private AdqDeviceManager deviceManager;
  private Adq114DeviceImpl device;

  private Thread acquisitionThread;

  @Override
  public void start(Stage stage) {
    deviceManager = new AdqDeviceManager(LOG);
    device = new Adq114DeviceImpl(deviceManager, LOG);

    var chart = new ContinuousFunctionChart<>(
        new QuantityAxis<>(new MetricTickUnits<>(second())),
        new QuantityAxis<>(new MetricTickUnits<>(count())).setPaddingApplied(true));
    chart.setTitle(device.toString());
    var series = chart.addSeries();

    stage.setTitle(device.getProductId() + " - " + device.getProductFamily());
    stage.setScene(new Scene(chart));
    stage.show();

    var accumulator = new ContinuousFunctionAccumulator<>(
        device.acquisitionDataEvents(),
        device.getSampleDomain(),
        device.getSampleIntensityUnit());

    accumulator
        .accumulation()
        .weakReference(series)
        .observe(o -> o.owner().setContinuousFunction(() -> o.message().getLatestAccumulation()));

    acquisitionThread = new Thread(() -> {
      try (var controller = device.acquireControl(0, TimeUnit.SECONDS)) {
        controller.setTriggerMode(SOFTWARE);
        controller.setTestPatternMode(NORMAL_OPERATION);
        controller.setAccumulationsPerAcquisition(8);
        controller.setAcquisitionCount(128);
        controller.setSampleDepth(512 * 32);

        controller.startAcquisition();
      } catch (TimeoutException | InterruptedException e) {
        e.printStackTrace();
      }
    });
    acquisitionThread.start();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
    if (acquisitionThread != null) {
      acquisitionThread.join();
    }
    if (deviceManager != null) {
      deviceManager.close();
    }
  }
}
