package uk.co.saiman.instrument.stage.copley;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;

import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.Int32;
import uk.co.saiman.comms.copley.MotorAxis;
import uk.co.saiman.instrument.stage.StageDimension;
import uk.co.saiman.measurement.Units;
import uk.co.strangeskies.mathematics.Interval;

public class CopleyLinearDimension implements StageDimension<Length> {
  private final Units units;
  private final MotorAxis axis;
  private final CopleyController controller;

  public CopleyLinearDimension(Units units, MotorAxis axis, CopleyController controller) {
    this.units = units;
    this.axis = axis;
    this.controller = controller;
  }

  @Override
  public Unit<Length> getUnit() {
    return units.metre().get();
  }

  @Override
  public Interval<Quantity<Length>> getBounds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void requestPosition(Quantity<Length> offset) {
    int micrometreOffset = offset.to(units.metre().micro().get()).getValue().intValue();

    controller.getRequestedPosition().set(axis, new Int32(micrometreOffset));
  }

  @Override
  public Quantity<Length> getRequestedPosition() {
    return units.metre().micro().getQuantity(controller.getRequestedPosition().get(axis).value);
  }

  @Override
  public Quantity<Length> getActualPosition() {
    return units.metre().micro().getQuantity(controller.getActualPosition().get(axis).value);
  }
}
