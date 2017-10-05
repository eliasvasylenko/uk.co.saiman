package uk.co.saiman.msapex.instrument;

import static uk.co.saiman.instrument.InstrumentLifecycleState.BEGIN_OPERATION;
import static uk.co.saiman.instrument.InstrumentLifecycleState.OPERATING;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.internal.events.EventBroker;

import uk.co.saiman.instrument.Instrument;

public class InstrumentStandbyHandler {
  @Execute
  void execute(IEclipseContext context, Instrument instrument, EventBroker eventBroker) {
    instrument.requestStandby();
  }

  @CanExecute
  boolean canExecute(Instrument instrument) {
    return instrument.lifecycleState().isEqual(BEGIN_OPERATION)
        || instrument.lifecycleState().isEqual(OPERATING);
  }
}
