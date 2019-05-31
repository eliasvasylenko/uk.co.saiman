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

import static java.util.concurrent.TimeUnit.SECONDS;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS_FAILED;
import static uk.co.saiman.instrument.sample.SampleState.EXCHANGE_FAILED;

import java.util.concurrent.TimeoutException;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.internal.events.EventBroker;

import uk.co.saiman.instrument.sample.SampleDevice;
import uk.co.saiman.instrument.sample.SampleState;

public class RequestExchangeHandler {
  @Execute
  void execute(IEclipseContext context, SampleDevice<?, ?> device, EventBroker eventBroker)
      throws TimeoutException,
      InterruptedException {
    try (var control = device.acquireControl(1, SECONDS)) {
      control.requestExchange();
    }
  }

  @CanExecute
  boolean canExecute(@Optional SampleState state) {
    return state != null
        && (state == ANALYSIS || state == ANALYSIS_FAILED || state == EXCHANGE_FAILED);
  }
}
