package uk.co.saiman.msapex.experiment.spectrum;

import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.spectrum.Spectrum;
import uk.co.saiman.experiment.spectrum.SpectrumProperties;
import uk.co.saiman.msapex.chart.ContinuousFunctionChartController;

public class SpectrumGraphEditorPart {
  @Inject
  @Localize
  SpectrumProperties properties;

  @Inject
  MDirtyable dirty;

  @Inject
  Result<Spectrum> result;

  @FXML
  private ContinuousFunctionChartController spectrumGraphController;

  @PostConstruct
  void postConstruct(BorderPane container, @LocalInstance FXMLLoader loaderProvider) {
    container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());

    System.out.println("[SGEP] result! " + result.tryGet().orElse(null));

    result.optional().weakReference(this).observe(m -> m.owner().setResultData(m.message()));
  }

  private void setResultData(Optional<Spectrum> data) {
    Platform.runLater(() -> {
      spectrumGraphController.getContinuousFunctions().clear();
      data.ifPresent(d -> spectrumGraphController.getContinuousFunctions().add(d.getRawData()));
    });

    dirty.setDirty(true);
  }

  @FXML
  void initialize() {
    // noSelectionLabel.textProperty().bind(wrap(text.noSampleSources()));
  }
}
