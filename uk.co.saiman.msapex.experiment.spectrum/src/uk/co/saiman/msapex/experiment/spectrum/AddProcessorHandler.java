package uk.co.saiman.msapex.experiment.spectrum;

import static uk.co.saiman.fx.FxUtilities.wrap;

import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.fx.core.di.Service;

import javafx.scene.control.ChoiceDialog;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.spectrum.SpectrumConfiguration;
import uk.co.saiman.experiment.spectrum.SpectrumProcessorType;
import uk.co.saiman.text.properties.Localized;

public class AddProcessorHandler {
  @Execute
  void execute(
      ExperimentNode<? extends SpectrumConfiguration, ?> node,
      @Service List<SpectrumProcessorType> processors,
      @Localize ExperimentProperties text) {
    requestProcessorType(
        processors,
        text.addSpectrumProcessor(),
        text.addSpectrumProcessorDescription())
            .ifPresent(processor -> node.getState().getProcessing().add(processor));
  }

  static java.util.Optional<SpectrumProcessorType> requestProcessorType(
      @Service List<SpectrumProcessorType> processors,
      Localized<String> title,
      Localized<String> header) {
    ChoiceDialog<SpectrumProcessorType> nameDialog = processors.isEmpty()
        ? new ChoiceDialog<>()
        : new ChoiceDialog<>(processors.get(0), processors);
    nameDialog.titleProperty().bind(wrap(title));
    nameDialog.headerTextProperty().bind(wrap(header));

    return nameDialog.showAndWait();
  }
}
