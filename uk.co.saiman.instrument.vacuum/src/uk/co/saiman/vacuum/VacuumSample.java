package uk.co.saiman.vacuum;

import javax.measure.Quantity;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Time;

public class VacuumSample {
  private final Quantity<Pressure> pressureMeasurement;
  private final Quantity<Time> sampleTime;

  public VacuumSample(Quantity<Pressure> pressureMeasurement, Quantity<Time> sampleTime) {
    this.pressureMeasurement = pressureMeasurement;
    this.sampleTime = sampleTime;
  }

  public Quantity<Pressure> getMeasuredPressure() {
    return pressureMeasurement;
  }

  public Quantity<Time> getMeasuredTime() {
    return sampleTime;
  }
}
