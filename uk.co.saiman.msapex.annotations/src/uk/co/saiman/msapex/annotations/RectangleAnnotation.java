package uk.co.saiman.msapex.annotations;

import static uk.co.saiman.fx.bindings.FluentObjectBinding.over;

import javax.measure.Quantity;

import javafx.beans.binding.ObjectBinding;
import javafx.scene.shape.Rectangle;
import uk.co.saiman.measurement.scalar.Scalar;

public class RectangleAnnotation<X extends Quantity<X>, Y extends Quantity<Y>>
    extends XYAnnotation<X, Y> {
  private final Rectangle shape;

  public RectangleAnnotation(Quantity<X> width, Quantity<Y> height) {
    this.shape = new Rectangle();
    this.shape.setStrokeWidth(1);
    getChildren().add(shape);

    var zeroX = new Scalar<>(width.getUnit(), 0);
    var zeroY = new Scalar<>(height.getUnit(), 0);

    ObjectBinding<Quantity<X>> relativeWidth = over(layoutUnitX())
        .map(u -> width.to(u).subtract(zeroX.to(u)));
    shape.widthProperty().bind(measurementToLayoutWidth(relativeWidth));

    ObjectBinding<Quantity<Y>> relativeHeight = over(layoutUnitY())
        .map(u -> height.to(u).subtract(zeroY.to(u)));
    shape.heightProperty().bind(measurementToLayoutHeight(relativeHeight));
  }
}
