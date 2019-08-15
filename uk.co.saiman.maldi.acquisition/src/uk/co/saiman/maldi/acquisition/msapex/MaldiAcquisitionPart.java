package uk.co.saiman.maldi.acquisition.msapex;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
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
  public void setEnvironment(@Optional GlobalEnvironment environment) {
    if (environment == null) {
      unsetDevice();
      return;
    }

    if (environment.providesValue(AcquisitionDevice.class)) {
      try {
        setDevice(environment.provideValue(AcquisitionDevice.class));
      } catch (Exception e) {
        setDeviceFailed();
      }
    } else {
      unsetDevice();
    }
  }

  public void setDevice(AcquisitionDevice<?> device) {
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
