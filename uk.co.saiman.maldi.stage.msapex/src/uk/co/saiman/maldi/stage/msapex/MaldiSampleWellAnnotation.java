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
 * This file is part of uk.co.saiman.maldi.stage.msapex.
 *
 * uk.co.saiman.maldi.stage.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.stage.msapex;

import static java.util.Objects.requireNonNull;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.YELLOW;
import static uk.co.saiman.maldi.stage.msapex.MaldiSampleWellAnnotation.State.EXCLUDED;

import javax.measure.quantity.Length;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import uk.co.saiman.maldi.sampleplates.MaldiSampleWell;
import uk.co.saiman.msapex.annotations.EllipseAnnotation;

public class MaldiSampleWellAnnotation extends EllipseAnnotation<Length, Length> {
  enum State {
    EXCLUDED(
        BLACK),
    INCLUDED(WHITE),
    CONDUCTED(BLUE),
    RUNNING(YELLOW),
    FAILED(RED),
    COMPLETE(GREEN);

    private final Color color;

    State(Color color) {
      this.color = color;
    }

    public Color getColor() {
      return color;
    }
  }

  private static final double SELECTED_ALPHA = 0.6;
  private static final double UNSELECTED_ALPHA = 0.3;

  private final MaldiSampleWell sampleWell;

  private final BooleanProperty selected;
  private final ObjectProperty<State> state;

  public MaldiSampleWellAnnotation(MaldiSampleWell sampleWell) {
    super(sampleWell.radius(), sampleWell.radius());

    this.sampleWell = sampleWell;

    getShape().setStrokeWidth(1);

    setMeasurementX(sampleWell.rest().getX());
    setMeasurementY(sampleWell.rest().getY());

    this.selected = new SimpleBooleanProperty(false);
    this.state = new SimpleObjectProperty<>(EXCLUDED);

    selected.addListener(c -> updateFill());
    state.addListener(c -> updateFill());
    updateFill();
  }

  void updateFill() {
    var fill = getState()
        .getColor()
        .deriveColor(1, 1, 1, isSelected() ? SELECTED_ALPHA : UNSELECTED_ALPHA);
    var stroke = getState().getColor();

    getShape().setFill(fill);
    getShape().setStroke(stroke);
  }

  public MaldiSampleWell getSampleWell() {
    return sampleWell;
  }

  public BooleanProperty selectedProperty() {
    return selected;
  }

  public boolean isSelected() {
    return selected.get();
  }

  public void setSelected(boolean value) {
    selected.set(value);
  }

  public ObjectProperty<State> stateProperty() {
    return state;
  }

  public State getState() {
    return state.get();
  }

  public void setState(State value) {
    state.set(requireNonNull(value));
  }
}
