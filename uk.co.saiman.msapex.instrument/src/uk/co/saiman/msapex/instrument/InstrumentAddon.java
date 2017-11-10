/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.fx.core.di.Service;

import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.log.Log;

/**
 * Register a camera device in the application context whenever one becomes
 * available and there is no camera already registered.
 *
 * @author Elias N Vasylenko
 */
public class InstrumentAddon {
  public static final String INSTRUMENT_TOOLBAR_OPERATE = "uk.co.saiman.msapex.instrument.handledtoolitem.operate";
  public static final String INSTRUMENT_TOOLBAR_STANDBY = "uk.co.saiman.msapex.instrument.handledtoolitem.standby";

  public static final String INSTRUMENT_MENU_OPERATE = "uk.co.saiman.msapex.instrument.handledmenuitem.operate";
  public static final String INSTRUMENT_MENU_STANDBY = "uk.co.saiman.msapex.instrument.handledmenuitem.standby";

  @Inject
  @Service
  private Instrument instrument;

  @Inject
  private IEclipseContext context;

  @Optional
  @Inject
  @Service
  private Log log;

  @PostConstruct
  void initialize(EventBroker eventBroker) {
    context.set(Instrument.class, instrument);

    instrument.lifecycleState().observe(s -> {
      eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, INSTRUMENT_TOOLBAR_OPERATE);
      eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, INSTRUMENT_TOOLBAR_STANDBY);
      eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, INSTRUMENT_MENU_OPERATE);
      eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, INSTRUMENT_MENU_STANDBY);
    });
  }
}
