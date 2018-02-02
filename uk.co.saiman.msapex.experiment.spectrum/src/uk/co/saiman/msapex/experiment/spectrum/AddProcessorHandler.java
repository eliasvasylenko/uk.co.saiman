package uk.co.saiman.msapex.experiment.spectrum;

import static uk.co.saiman.fx.FxUtilities.wrap;

import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.fx.core.di.Service;

import javafx.scene.control.ChoiceDialog;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.processing.ProcessorType;
import uk.co.saiman.experiment.spectrum.SpectrumResultConfiguration;
import uk.co.saiman.text.properties.Localized;

public class AddProcessorHandler {
  @Execute
  void execute(
      ExperimentNode<? extends SpectrumResultConfiguration, ?> node,
      @Service List<ProcessorType<?>> processors,
      @Localize ExperimentProperties text) {
    requestProcessorType(
        processors,
        text.addSpectrumProcessor(),
        text.addSpectrumProcessorDescription())
            .ifPresent(
                processor -> node
                    .getState()
                    .getProcessing()
                    .add(processor.configure(new PersistedState())));
  }

  static java.util.Optional<ProcessorType<?>> requestProcessorType(
      @Service List<ProcessorType<?>> processors,
      Localized<String> title,
      Localized<String> header) {
    ChoiceDialog<ProcessorType<?>> nameDialog = processors.isEmpty()
        ? new ChoiceDialog<>()
        : new ChoiceDialog<>(processors.get(0), processors);
    nameDialog.titleProperty().bind(wrap(title));
    nameDialog.headerTextProperty().bind(wrap(header));

    return nameDialog.showAndWait();
  }
}
