package uk.co.saiman.msapex.instrument.sample;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;

import javafx.scene.Group;
import javafx.scene.control.Label;
import uk.co.saiman.instrument.sample.SampleDevice;
import uk.co.saiman.observable.Disposable;

public class StateToolControl {
  private final Label label;
  private SampleDevice<?> device;
  private Disposable requestedLocation;
  private Disposable actualLocation;
  private Disposable sampleState;

  @Inject
  public StateToolControl(Group parent) {
    label = new Label();
    parent.getChildren().add(label);
  }

  @Inject
  public void setDevice(@Optional SampleDevice<?> device) {
    if (this.device != null) {
      requestedLocation.cancel();
      actualLocation.cancel();
      sampleState.cancel();
    }

    this.device = device;
    if (device != null) {
      requestedLocation = device.requestedLocation().observe(l -> updateLabel());
      actualLocation = device.actualLocation().observe(l -> updateLabel());
      sampleState = device.sampleState().observe(l -> updateLabel());
    } else {
      requestedLocation = null;
      actualLocation = null;
      sampleState = null;
    }

    updateLabel();
  }

  void updateLabel() {
    if (device == null) {
      label.setText("No device!");
    } else {
      // label.setText(device.requestedLocation().get() + " " +
      // device.actualLocation().get());
      label.setText(device.sampleState().get().toString());
    }
  }
}
