/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.msapex.instrument.sample.
 *
 * uk.co.saiman.msapex.instrument.sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument.sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.instrument.sample;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;

import javafx.scene.Group;
import javafx.scene.control.Label;
import uk.co.saiman.instrument.sample.SampleDevice;
import uk.co.saiman.observable.Disposable;

public class StateToolControl {
  private final Label label;
  private SampleDevice<?, ?> device;
  private Disposable requestedLocation;
  private Disposable actualLocation;
  private Disposable sampleState;

  @Inject
  public StateToolControl(Group parent) {
    label = new Label();
    parent.getChildren().add(label);
  }

  @Inject
  public void setDevice(@Optional SampleDevice<?, ?> device) {
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
