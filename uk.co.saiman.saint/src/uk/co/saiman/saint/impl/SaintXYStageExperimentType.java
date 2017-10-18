package uk.co.saiman.saint.impl;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.ExperimentConfigurationContext;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.sample.XYStageExperimentType;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.saiman.measurement.Units;
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
  public String getID() {
    return getClass().getName();
  }

  @Override
  public SaintXYStageConfiguration createState(
      ExperimentConfigurationContext<SaintXYStageConfiguration> context) {
    String id = "A1";

    context.setID(context.getID().orElse("Sample " + id));

    return new SaintXYStageConfiguration() {
      private Quantity<Length> x = loadLength(X_STATE);
      private Quantity<Length> y = loadLength(Y_STATE);

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
        x = saveLength(X_STATE, offset);
      }

      @Override
      public void setY(Quantity<Length> offset) {
        y = saveLength(Y_STATE, offset);
      }

      @Override
      public Quantity<Length> getX() {
        return x;
      }

      @Override
      public Quantity<Length> getY() {
        return y;
      }

      private Quantity<Length> loadLength(String key) {
        return context
            .persistedState()
            .getString(key)
            .map(l -> units.parseQuantity(l).asType(Length.class))
            .orElseGet(() -> saveLength(key, units.metre().micro().getQuantity(0)));
      }

      private Quantity<Length> saveLength(String key, Quantity<Length> length) {
        context.persistedState().putString(key, units.formatQuantity(length));
        return length;
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
