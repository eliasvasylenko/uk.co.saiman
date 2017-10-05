package uk.co.saiman.msapex.instrument;

import static uk.co.saiman.instrument.InstrumentLifecycleState.STANDBY;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.internal.events.EventBroker;

import uk.co.saiman.instrument.Instrument;

public class InstrumentOperateHandler {
  @Execute
  void execute(IEclipseContext context, Instrument instrument, EventBroker eventBroker) {
    instrument.requestOperation();
  }

  @CanExecute
  boolean canExecute(Instrument instrument) {
    return instrument.lifecycleState().isEqual(STANDBY);
  }
}
