/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.msapex.annotations.
 *
 * uk.co.saiman.msapex.annotations is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.annotations is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
