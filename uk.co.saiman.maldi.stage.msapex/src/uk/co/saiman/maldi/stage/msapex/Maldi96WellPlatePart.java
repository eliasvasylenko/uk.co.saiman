/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.maldi.stage.msapex.
 *
 * uk.co.saiman.maldi.stage.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.stage.msapex;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javafx.scene.input.MouseButton.SECONDARY;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.measure.quantity.Length;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import uk.co.saiman.fx.bindings.FluentObjectBinding;
import uk.co.saiman.instrument.stage.msapex.SamplePlateDiagram;
import uk.co.saiman.maldi.sampleplates.Maldi96WellPlate;
import uk.co.saiman.maldi.sampleplates.MaldiSampleWell;
import uk.co.saiman.maldi.stage.MaldiStage;
import uk.co.saiman.msapex.annotations.AnnotationLayer;
import uk.co.saiman.msapex.annotations.DragAnnotation;

public class Maldi96WellPlatePart {
  private Maldi96WellPlate samplePlate;
  private final MaldiSamplePlateDiagram stageDiagram;
  private final MaldiSampleWellDiagram sampleWellDiagram;

  private final IEclipseContext context;

  private Map<MaldiSampleWell, MaldiSampleWellAnnotation> stageAnnotations = new HashMap<>();
  private Map<MaldiSampleWell, MaldiSampleWellAnnotation> sampleWellAnnotations = new HashMap<>();

  private DragAnnotation<Length, Length> dragAnnotation;

  @Inject
  public Maldi96WellPlatePart(
      IEclipseContext context,
      BorderPane container,
      MaldiStage stage,
      SamplePlatePresenter presenter) {
    this.context = context;

    HBox diagrams = new HBox();
    container.setLeft(diagrams);

    stageDiagram = new MaldiSamplePlateDiagram(
        stage,
        new Image(getClass().getClassLoader().getResourceAsStream("/slides/96-well.jpg")));
    fixRatioToHeight(stageDiagram, diagrams);

    sampleWellDiagram = new MaldiSampleWellDiagram(stage);
    fixRatioToHeight(sampleWellDiagram, diagrams);

    stageDiagram.setOnMouseClicked(e -> processClick(e, null));

    dragAnnotation = new DragAnnotation<>();
    stageDiagram.getAnnotationLayer().getAnnotations().add(dragAnnotation);
  }

  private void fixRatioToHeight(SamplePlateDiagram diagram, HBox container) {
    var ratio = FluentObjectBinding
        .over(diagram.getAnnotationLayer().measurementBoundsProperty())
        .mapToDouble(bounds -> bounds.getWidth() / bounds.getHeight());

    diagram.prefHeightProperty().bind(container.heightProperty());
    diagram.prefWidthProperty().bind(container.heightProperty().multiply(ratio));

    container.getChildren().add(diagram);
  }

  private synchronized void processClick(MouseEvent event, MaldiSampleWell sampleWell) {
    event.consume();

    SampleAreaSelection selection;

    if (event.isControlDown() || event.getButton().equals(SECONDARY)) {
      selection = context.get(SampleAreaSelection.class);
      if (selection == null) {
        selection = SampleAreaSelection.empty();
      }
    } else {
      selection = SampleAreaSelection.empty();
    }

    if (dragAnnotation.getDragBounds().isPresent()) {
      var bounds = dragAnnotation.getDragBounds().get();

      var inDragBox = stageAnnotations
          .values()
          .stream()
          .filter(s -> s.intersects(s.parentToLocal(bounds)))
          .map(MaldiSampleWellAnnotation::getSampleWell)
          .collect(toList());
      selection = selection.with(inDragBox);

    } else if (sampleWell != null) {
      if (selection.sampleAreas().anyMatch(sampleWell::equals)) {
        selection = selection.without(sampleWell);
      } else {
        selection = selection.with(sampleWell);
      }
    }

    context.modify(SampleAreaSelection.class, selection);
  }

  @Inject
  synchronized void update(
      @Optional SampleAreaSelection selection,
      @Optional Maldi96WellPlate samplePlate) {
    setSamplePlate(samplePlate);
    setSelection(selection);
  }

  synchronized void setSelection(SampleAreaSelection selection) {
    if (stageAnnotations != null) {
      var selectedItems = java.util.Optional
          .ofNullable(selection)
          .stream()
          .flatMap(SampleAreaSelection::sampleAreas)
          .collect(toSet());

      stageAnnotations.values().forEach(annotation -> {
        annotation.setSelected(selectedItems.contains(annotation.getSampleWell()));
      });
    }

    if (selection != null && selection.sampleAreas().count() == 1) {
      selection
          .sampleAreas()
          .findAny()
          .filter(MaldiSampleWell.class::isInstance)
          .map(MaldiSampleWell.class::cast)
          .ifPresent(s -> sampleWellDiagram.setSampleWell(s));
    }
  }

  synchronized void setSamplePlate(Maldi96WellPlate samplePlate) {
    if (this.samplePlate == samplePlate) {
      return;
    }

    this.samplePlate = samplePlate;

    stageDiagram.setSamplePlate(samplePlate);
    sampleWellDiagram.setSamplePlate(samplePlate);
    setSampleWellAnnotations(samplePlate, stageAnnotations, stageDiagram.getAnnotationLayer());
    setSampleWellAnnotations(
        samplePlate,
        sampleWellAnnotations,
        sampleWellDiagram.getAnnotationLayer());
  }

  private void setSampleWellAnnotations(
      Maldi96WellPlate samplePlate,
      Map<MaldiSampleWell, MaldiSampleWellAnnotation> annotations,
      AnnotationLayer<Length, Length> annotationLayer) {
    var newAnnotations = annotationLayer.getAnnotations();

    annotations.values().forEach(newAnnotations::remove);

    if (samplePlate == null) {
      annotations.clear();

    } else {
      samplePlate
          .sampleAreas()
          .forEach(sampleArea -> annotations.put(sampleArea, annotate(sampleArea)));
    }

    annotations.values().forEach(newAnnotations::add);
  }

  MaldiSampleWellAnnotation annotate(MaldiSampleWell sampleWell) {
    var annotation = new MaldiSampleWellAnnotation(sampleWell);
    annotation.setOnMouseClicked(e -> processClick(e, sampleWell));
    return annotation;
  }
}
