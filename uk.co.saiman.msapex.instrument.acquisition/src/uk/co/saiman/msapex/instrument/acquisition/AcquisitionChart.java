package uk.co.saiman.msapex.instrument.acquisition;

import static uk.co.saiman.measurement.Units.count;
import static uk.co.saiman.measurement.Units.second;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import javafx.scene.layout.Pane;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.saiman.msapex.chart.ContinuousFunctionChart;
import uk.co.saiman.msapex.chart.ContinuousFunctionSeries;
import uk.co.saiman.msapex.chart.MetricTickUnits;
import uk.co.saiman.msapex.chart.QuantityAxis;
import uk.co.saiman.observable.Disposable;

public class AcquisitionChart extends Pane {
  private final AcquisitionDevice<?> device;
  private final ContinuousFunctionSeries<Time, Dimensionless> series;
  private volatile Disposable observation;

  public AcquisitionChart(AcquisitionDevice<?> device) {
    this.device = device;

    ContinuousFunctionChart<Time, Dimensionless> chartController = new ContinuousFunctionChart<Time, Dimensionless>(
        new QuantityAxis<>(new MetricTickUnits<>(second())),
        new QuantityAxis<>(new MetricTickUnits<>(count())).setPaddingApplied(true));
    chartController.setTitle(device.getName());

    this.series = chartController.addSeries();

    getChildren().add(chartController);
  }

  public synchronized void open() {
    if (observation == null) {
      observation = device.dataEvents().observe(series::setContinuousFunction);
    }
  }

  public synchronized void close() {
    if (observation != null) {
      observation.cancel();
      observation = null;
    }
  }
}
