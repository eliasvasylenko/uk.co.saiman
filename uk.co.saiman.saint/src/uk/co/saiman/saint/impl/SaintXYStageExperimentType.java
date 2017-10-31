package uk.co.saiman.saint.impl;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.sample.XYStageExperimentType;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.property.Property;
import uk.co.saiman.saint.SaintXYStageConfiguration;

@Component
public class SaintXYStageExperimentType implements XYStageExperimentType<SaintXYStageConfiguration>,
    ExperimentType<SaintXYStageConfiguration> {
  private static final String X_STATE = "xOffset";
  private static final String Y_STATE = "yOffset";

  @Reference
  XYStageDevice stageDevice;

  @Reference
  Units units;

  @Override
  public String getId() {
    return getClass().getName();
  }

  @Override
  public SaintXYStageConfiguration createState(
      ConfigurationContext<SaintXYStageConfiguration> context) {
    String id = context.getId(() -> "A1");

    return new SaintXYStageConfiguration() {
      private final Property<Quantity<Length>> x = getLength(X_STATE);
      private final Property<Quantity<Length>> y = getLength(Y_STATE);

      private Property<Quantity<Length>> getLength(String value) {
        return context
            .persistedState()
            .stringValue(value)
            .map(l -> units.parseQuantity(l).asType(Length.class), units::formatQuantity)
            .setDefault(() -> units.metre().micro().getQuantity(0));
      }

      @Override
      public String getName() {
        return id;
      }

      @Override
      public XYStageDevice stageDevice() {
        return stageDevice;
      }

      @Override
      public void setX(Quantity<Length> offset) {
        x.set(offset);
      }

      @Override
      public void setY(Quantity<Length> offset) {
        y.set(offset);
      }

      @Override
      public Quantity<Length> getX() {
        return x.get();
      }

      @Override
      public Quantity<Length> getY() {
        return y.get();
      }

      @Override
      public String toString() {
        return "(" + units.formatQuantity(getX()) + ", " + units.formatQuantity(getY()) + ")";
      }
    };
  }

  @Override
  public XYStageDevice device() {
    return stageDevice;
  }
}
