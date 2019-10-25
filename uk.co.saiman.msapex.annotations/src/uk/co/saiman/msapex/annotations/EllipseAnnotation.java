package uk.co.saiman.msapex.annotations;

import static uk.co.saiman.fx.bindings.FluentObjectBinding.over;

import javax.measure.Quantity;

import javafx.beans.binding.ObjectBinding;
import javafx.scene.shape.Ellipse;
import uk.co.saiman.measurement.scalar.Scalar;

public class EllipseAnnotation<X extends Quantity<X>, Y extends Quantity<Y>>
    extends XYAnnotation<X, Y> {
  private final Ellipse shape;

  public EllipseAnnotation(Quantity<X> radiusX, Quantity<Y> radiusY) {
    this.shape = new Ellipse();
    getChildren().add(shape);

    var zeroX = new Scalar<>(radiusX.getUnit(), 0);
    var zeroY = new Scalar<>(radiusY.getUnit(), 0);

    ObjectBinding<Quantity<X>> relativeWidth = over(layoutUnitX())
        .map(u -> radiusX.to(u).subtract(zeroX.to(u)));
    shape.radiusXProperty().bind(measurementToLayoutWidth(relativeWidth));

    ObjectBinding<Quantity<Y>> relativeHeight = over(layoutUnitY())
        .map(u -> radiusY.to(u).subtract(zeroY.to(u)));
    shape.radiusYProperty().bind(measurementToLayoutHeight(relativeHeight));
  }

  protected Ellipse getShape() {
    return shape;
  }
}
