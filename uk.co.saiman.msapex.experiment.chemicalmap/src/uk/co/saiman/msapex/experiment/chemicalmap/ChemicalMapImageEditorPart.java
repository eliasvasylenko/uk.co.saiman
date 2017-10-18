package uk.co.saiman.msapex.experiment.chemicalmap;

import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.chemicalmap.ChemicalMap;
import uk.co.saiman.experiment.chemicalmap.ChemicalMapProperties;

public class ChemicalMapImageEditorPart {
  @Inject
  @Localize
  ChemicalMapProperties properties;

  @Inject
  MDirtyable dirty;

  @Inject
  Result<ChemicalMap> result;

  @PostConstruct
  void postConstruct(BorderPane container, @LocalInstance FXMLLoader loaderProvider) {
    container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());
    System.out.println("PC: ChemicalMapImageEditorPart");
  }

  @FXML
  void initialize() {
    // noSelectionLabel.textProperty().bind(wrap(text.noSampleSources()));
  }
}
