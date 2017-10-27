package uk.co.saiman.msapex.experiment.spectrum;

import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.experiment.spectrum.Spectrum;
import uk.co.saiman.experiment.spectrum.SpectrumProperties;
import uk.co.saiman.msapex.chart.ContinuousFunctionChartController;

public class SpectrumGraphEditorPart {
  @Inject
  @Localize
  SpectrumProperties properties;

  @Inject
  MDirtyable dirty;

  @FXML
  private ContinuousFunctionChartController spectrumGraphController;

  @PostConstruct
  void postConstruct(
      BorderPane container,
      @LocalInstance FXMLLoader loaderProvider,
      IEclipseContext context) {
    container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());

    System.out.println("[SGEP]");
  }

  @Inject
  public void setResultData(@Optional Spectrum data) {
    System.out.println("[SGEP] set data! " + data);

    Platform.runLater(() -> {
      spectrumGraphController.getContinuousFunctions().clear();
      if (data != null)
        spectrumGraphController.getContinuousFunctions().add(data.getRawData());
    });

    dirty.setDirty(true);
  }

  @FXML
  void initialize() {
    // noSelectionLabel.textProperty().bind(wrap(text.noSampleSources()));
  }
}
