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
