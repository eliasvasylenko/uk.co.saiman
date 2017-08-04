package uk.co.saiman.instrument.stage.copley;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.Int32;
import uk.co.saiman.comms.copley.MotorAxis;
import uk.co.saiman.instrument.stage.StageDimension;
import uk.co.saiman.measurement.Units;

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
  public Quantity<Length> getMinimumOffset() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Quantity<Length> getMaximumOffset() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void requestOffset(Quantity<Length> offset) {
    int micrometreOffset = offset.to(units.metre().micro().get()).getValue().intValue();

    controller.getRequestedPosition().set(axis, new Int32(micrometreOffset));
  }

  @Override
  public Quantity<Length> getRequestedOffset() {
    return units.metre().micro().getQuantity(controller.getRequestedPosition().get(axis).value);
  }

  @Override
  public Quantity<Length> getActualOffset() {
    return units.metre().micro().getQuantity(controller.getActualPosition().get(axis).value);
  }
}
