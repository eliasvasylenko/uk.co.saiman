package uk.co.saiman.msapex.annotations;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class AnnotatedImage<X extends Quantity<X>, Y extends Quantity<Y>> extends StackPane {
  private final ImageView imageView;
  private final AnnotationLayer<X, Y> annotationLayer;

  protected AnnotatedImage(Unit<X> unitX, Unit<Y> unitY) {
    this.imageView = new ImageView();
    this.annotationLayer = new AnnotationLayer<>(unitX, unitY);

    getChildren().add(imageView);
    getChildren().add(annotationLayer);

    imageView.fitWidthProperty().bind(annotationLayer.widthProperty());
    imageView.fitHeightProperty().bind(annotationLayer.heightProperty());
    imageView.layoutXProperty().bind(annotationLayer.layoutXProperty());
    imageView.layoutYProperty().bind(annotationLayer.layoutYProperty());
    imageView.setPreserveRatio(false);
    imageView.setManaged(false);
  }

  public Image getImage() {
    return imageView.getImage();
  }

  protected void setImage(Image image) {
    this.imageView.setImage(image);
  }

  public AnnotationLayer<X, Y> getAnnotationLayer() {
    return annotationLayer;
  }
}
