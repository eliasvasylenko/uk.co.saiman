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
 * This file is part of uk.co.saiman.msapex.instrument.
 *
 * uk.co.saiman.msapex.instrument is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.instrument;

import static org.eclipse.e4.ui.workbench.UIEvents.ALL_ELEMENT_ID;
import static org.eclipse.e4.ui.workbench.UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.fx.core.di.Service;

import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.InstrumentLifecycleState;
import uk.co.saiman.log.Log;

/**
 * Register a camera device in the application context whenever one becomes
 * available and there is no camera already registered.
 *
 * @author Elias N Vasylenko
 */
public class InstrumentAddon {
  @Inject
  @Service
  private Instrument instrument;

  @Inject
  private IEclipseContext context;

  @Inject
  @Service
  private Log log;

  @PostConstruct
  void initialize() {
    context.set(Instrument.class, instrument);

    instrument.lifecycleState().value().weakReference(this).observe(o -> {
      var event = o.message();
      o.owner().context.set(InstrumentLifecycleState.class, event);
    });
    context.set(InstrumentLifecycleState.class, instrument.lifecycleState().get());
  }

  @Inject
  public void stateChange(EventBroker eventBroker, @Optional InstrumentLifecycleState state) {
    eventBroker.send(REQUEST_ENABLEMENT_UPDATE_TOPIC, ALL_ELEMENT_ID);
  }
}
