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

import static javafx.scene.input.MouseEvent.DRAG_DETECTED;
import static javafx.scene.input.MouseEvent.MOUSE_DRAGGED;
import static javafx.scene.input.MouseEvent.MOUSE_ENTERED;
import static javafx.scene.input.MouseEvent.MOUSE_EXITED;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.CORNFLOWERBLUE;

import java.util.Optional;

import javax.measure.Quantity;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class DragAnnotation<X extends Quantity<X>, Y extends Quantity<Y>> extends Annotation<X, Y> {
  private final Rectangle rectangle;
  private Point2D dragStart;
  private final ObjectProperty<InvalidationListener> onDragCompleted;

  private final EventHandler<MouseEvent> events;
  private AnnotationLayer<X, Y> annotationLayer;

  private boolean dragging = false;
  private Bounds dragged;

  public DragAnnotation() {
    this.rectangle = new Rectangle();
    this.dragStart = new Point2D(0, 0);
    this.onDragCompleted = new SimpleObjectProperty<>();
    this.events = event -> {
      if (event.getEventType() == MOUSE_PRESSED) {
        dragging = false;
        dragged = null;
        dragStart = new Point2D(event.getX(), event.getY());

      } else if (event.getEventType() == MOUSE_ENTERED) {
        if (dragging) {
          rectangle.setVisible(true);
        }

      } else if (event.getEventType() == MOUSE_EXITED) {
        rectangle.setVisible(false);

      } else if (event.getEventType() == DRAG_DETECTED) {
        dragging = true;
        rectangle.setX(dragStart.getX());
        rectangle.setY(dragStart.getY());
        rectangle.setHeight(1);
        rectangle.setWidth(1);
        rectangle.setVisible(true);

      } else if (event.getEventType() == MOUSE_RELEASED) {
        if (dragging) {
          dragging = false;
          dragged = new BoundingBox(
              rectangle.getX(),
              rectangle.getY(),
              rectangle.getWidth(),
              rectangle.getHeight());
          Optional.ofNullable(onDragCompleted.get()).ifPresent(c -> c.invalidated(onDragCompleted));
        }
        rectangle.setVisible(false);

      } else if (event.getEventType() == MOUSE_DRAGGED) {
        if (dragging) {
          rectangle.setX(Math.min(event.getX(), dragStart.getX()));
          rectangle.setY(Math.min(event.getY(), dragStart.getY()));

          rectangle.setWidth(Math.abs(event.getX() - dragStart.getX()));
          rectangle.setHeight(Math.abs(event.getY() - dragStart.getY()));
        }
      }
    };

    rectangle.setFill(CORNFLOWERBLUE.deriveColor(1, 1, 1, 0.5));
    rectangle.setWidth(1);
    rectangle.setStroke(BLACK);

    getChildren().add(rectangle);

    annotationLayerProperty().addListener(c -> setAnnotationLayer(getAnnotationLayer()));
  }

  private void setAnnotationLayer(AnnotationLayer<X, Y> annotationLayer) {
    if (this.annotationLayer != null) {
      this.annotationLayer.removeEventHandler(MouseEvent.ANY, events);
    }
    this.annotationLayer = annotationLayer;
    if (this.annotationLayer != null) {
      this.annotationLayer.addEventHandler(MouseEvent.ANY, events);
    }
  }

  public ObjectProperty<InvalidationListener> onDragCompletedProperty() {
    return onDragCompleted;
  }

  public InvalidationListener getOnDragCompleted() {
    return onDragCompleted.get();
  }

  public void setOnDragCompleted(InvalidationListener value) {
    onDragCompleted.set(value);
  }

  public Optional<Bounds> getDragBounds() {
    return Optional.ofNullable(dragged);
  }
}
