package uk.co.saiman.msapex.instrument.sample;

import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS_LOCATION_FAILED;
import static uk.co.saiman.instrument.sample.SampleState.EXCHANGE;
import static uk.co.saiman.instrument.sample.SampleState.EXCHANGE_FAILED;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.internal.events.EventBroker;

import uk.co.saiman.instrument.sample.SampleDevice;

public class RequestAnalysisHandler {
  @Execute
  void execute(IEclipseContext context, SampleDevice<?> device, EventBroker eventBroker) {
    device.requestAnalysis();
  }

  @CanExecute
  boolean canExecute(@Optional SampleDevice<?> device) {
    return device != null
        && device
            .sampleState()
            .isMatching(
                s -> s == EXCHANGE || s == ANALYSIS_LOCATION_FAILED || s == EXCHANGE_FAILED);
  }
}
