package uk.co.saiman.maldi.stage;

import java.util.concurrent.TimeUnit;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.XYStageController;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class MaldiStageController implements XYStageController {
  private final XYStageController controller;

  public MaldiStageController(XYStageController controller) {
    this.controller = controller;
  }

  @Override
  public void requestExchange() {
    controller.requestExchange();
  }

  @Override
  public void requestReady() {
    controller.requestReady();
  }

  @Override
  public void requestAnalysis(XYCoordinate<Length> position) {
    controller.requestAnalysis(position);
  }

  @Override
  public SampleState<XYCoordinate<Length>> awaitRequest(long timeout, TimeUnit unit) {
    return controller.awaitRequest(timeout, unit);
  }

  @Override
  public SampleState<XYCoordinate<Length>> awaitReady(long timeout, TimeUnit unit) {
    return controller.awaitReady(timeout, unit);
  }

  @Override
  public void close() {
    controller.close();
  }

  @Override
  public boolean isClosed() {
    return controller.isClosed();
  }
}
