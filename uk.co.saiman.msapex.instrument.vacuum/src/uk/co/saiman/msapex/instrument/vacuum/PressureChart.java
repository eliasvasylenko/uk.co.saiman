package uk.co.saiman.msapex.instrument.vacuum;

import static uk.co.saiman.measurement.Units.pascal;
import static uk.co.saiman.measurement.Units.second;

import java.util.ArrayDeque;
import java.util.Deque;

import javax.measure.Quantity;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Time;

import javafx.scene.Node;
import uk.co.saiman.data.function.ArraySampledContinuousFunction;
import uk.co.saiman.data.function.IrregularSampledDomain;
import uk.co.saiman.instrument.vacuum.VacuumDevice;
import uk.co.saiman.instrument.vacuum.VacuumSample;
import uk.co.saiman.msapex.chart.ContinuousFunctionChart;
import uk.co.saiman.msapex.chart.ContinuousFunctionSeries;
import uk.co.saiman.msapex.chart.MetricTickUnits;
import uk.co.saiman.msapex.chart.QuantityAxis;

public class PressureChart {
  private final ContinuousFunctionChart<Time, Pressure> chart;
  private final Deque<VacuumSample> samples;

  public PressureChart(VacuumDevice<?> vacuumDevice) {
    this.chart = new ContinuousFunctionChart<Time, Pressure>(
        new QuantityAxis<>(new MetricTickUnits<>(second())),
        new QuantityAxis<>(new MetricTickUnits<>(pascal())).setPaddingApplied(true));
    this.chart.setTitle(vacuumDevice.getName());

    this.samples = new ArrayDeque<>();

    /*
     * Add latest data to chart controller
     */
    ContinuousFunctionSeries<Time, Pressure> series = chart.addSeries();
    vacuumDevice.sampleEvents().observe(sample -> updateSeries(series, sample));
  }

  private void updateSeries(ContinuousFunctionSeries<Time, Pressure> series, VacuumSample sample) {
    var function = new ArraySampledContinuousFunction<>(
        new IrregularSampledDomain<>(
            second().getUnit(),
            samples
                .stream()
                .map(VacuumSample::getMeasuredTime)
                .map(q -> q.to(second().getUnit()))
                .map(Quantity::getValue)
                .mapToDouble(Number::doubleValue)
                .toArray()),
        pascal().getUnit(),
        samples
            .stream()
            .map(VacuumSample::getMeasuredPressure)
            .map(q -> q.to(pascal().getUnit()))
            .map(Quantity::getValue)
            .mapToDouble(Number::doubleValue)
            .toArray());
    series.setContinuousFunction(function);
  }

  public Node getNode() {
    return chart;
  }
}
