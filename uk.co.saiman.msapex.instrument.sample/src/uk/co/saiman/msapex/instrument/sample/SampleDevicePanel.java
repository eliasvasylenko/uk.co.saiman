/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import javafx.scene.layout.Pane;
import uk.co.saiman.instrument.sample.SampleDevice;

/**
 * A user interface component for a sample device, to be displayed within the
 * sample part.
 * 
 * @author Elias N Vasylenko
 */
public interface SampleDevicePanel {
  SampleDevice<?, ?> device();

  /**
   * The class to be instantiated by e4 model injection where the UI is needed.
   * The JavaFX container for the UI element is injected as a {@link Pane}. The
   * device is also injected.
   * 
   * @return the class to instantiate
   */
  Class<?> paneModelClass();
}
