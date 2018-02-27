package uk.co.saiman.msapex.annotations;

import static uk.co.saiman.fx.FxUtilities.asSet;
import static uk.co.saiman.fx.FxUtilities.map;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.geometry.BoundingBox;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public class AnnotationLayer<X extends Quantity<X>, Y extends Quantity<Y>> extends Region {
  /*
   * TODO this is needed for the Bindings.select(...) statements in Annotation.
   * Frankly it is an utterly stupid API, hopefully Oracle will update this with
   * something type-safe.
   */
  static final String MEASUREMENT_BOUNDS = "measurementBounds";

  private final Unit<X> unitX;
  private final Unit<Y> unitY;

  private final ObjectProperty<BoundingBox> measurementBounds;

  protected AnnotationLayer(Unit<X> unitX, Unit<Y> unitY) {
    this.unitX = unitX;
    this.unitY = unitY;

    measurementBounds = new SimpleObjectProperty<>(new BoundingBox(0, 0, 1, 1));
  }

  @SuppressWarnings("unchecked")
  public ObservableSet<Annotation<X, Y>> getAnnotations() {
    return asSet(map(super.getChildren(), c -> (Annotation<X, Y>) c, c -> c));
  }

  public ObjectProperty<BoundingBox> measurementBoundsProperty() {
    return measurementBounds;
  }

  public BoundingBox getMeasurementBounds() {
    return measurementBounds.getValue();
  }

  public void setMeasurementBounds(BoundingBox value) {
    measurementBounds.setValue(value);
  }

  public Unit<X> getUnitX() {
    return unitX;
  }

  public Unit<Y> getUnitY() {
    return unitY;
  }

  public double measurementToLocalWidth(double measurement) {
    return measurement * getWidth() / measurementBounds.getValue().getWidth();
  }

  public double measurementToLocalHeight(double measurement) {
    return measurement * getHeight() / measurementBounds.getValue().getHeight();
  }

  public double localWidthToMeasurement(double local) {
    return local * measurementBounds.getValue().getWidth() / getWidth();
  }

  public double localHeightToMeasurement(double local) {
    return local * measurementBounds.getValue().getHeight() / getHeight();
  }

  public double measurementToLocalX(double measurement) {
    return measurementToLocalWidth(measurement - measurementBounds.getValue().getMinX());
  }

  public double measurementToLocalY(double measurement) {
    return measurementToLocalHeight(measurement - measurementBounds.getValue().getMinY());
  }

  public double localXToMeasurement(double local) {
    return localWidthToMeasurement(local) + measurementBounds.getValue().getMinX();
  }

  public double localYToMeasurement(double local) {
    return localHeightToMeasurement(local) + measurementBounds.getValue().getMinY();
  }

  @Override
  protected void layoutChildren() {
    for (Node node : getChildren()) {
      node.autosize();
    }
  }
}
