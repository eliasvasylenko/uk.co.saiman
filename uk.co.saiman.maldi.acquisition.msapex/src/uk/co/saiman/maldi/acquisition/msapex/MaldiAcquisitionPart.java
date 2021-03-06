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
 * This file is part of uk.co.saiman.maldi.acquisition.msapex.
 *
 * uk.co.saiman.maldi.acquisition.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.acquisition.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.acquisition.msapex;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.saiman.instrument.acquisition.msapex.AcquisitionChart;
import uk.co.saiman.instrument.msapex.device.DevicePresentationService;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

public class MaldiAcquisitionPart {
  static final String OSGI_SERVICE = "osgi.service";

  @Inject
  private DevicePresentationService devicePresentation;

  @Inject
  private Log log;

  private AcquisitionChart chart;

  @Inject
  private BorderPane container;

  @Inject
  public void setEnvironment(@Optional Environment environment) {
    if (environment == null) {
      unsetDevice();
      return;
    }

    if (environment.providesResource(AcquisitionDevice.class)) {
      try {
        setDevice(environment.provideResource(AcquisitionDevice.class).value());
      } catch (Exception e) {
        setDeviceFailed();
      }
    } else {
      unsetDevice();
    }
  }

  public void setDevice(AcquisitionDevice device) {
    Platform.runLater(() -> {
      System.out.println(" happy with : " + device);
      synchronized (this) {
        try {
          chart = new AcquisitionChart(device, devicePresentation);
          chart.open();
          container.centerProperty().set(chart);
        } catch (Exception e) {
          log.log(Level.ERROR, e);
        }
      }
    });
  }

  public void unsetDevice() {
    Platform.runLater(() -> {
      System.out.println(" sad 1 :(");
      synchronized (this) {
        try {
          if (chart != null) {
            chart.close();
            chart = null;
            container.centerProperty().set(new Label("missing acquisition device"));
          }
        } catch (Exception e) {
          log.log(Level.ERROR, e);
        }
      }
    });
  }

  public void setDeviceFailed() {
    Platform.runLater(() -> {
      System.out.println(" sad 2 :(");
      synchronized (this) {
        try {
          if (chart != null) {
            chart.close();
            chart = null;
            container.centerProperty().set(new Label("missing acquisition device"));
          }
        } catch (Exception e) {
          log.log(Level.ERROR, e);
        }
      }
    });
  }
}
