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

    setMeasurementX(sampleWell.center().getX());
    setMeasurementY(sampleWell.center().getY());

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
