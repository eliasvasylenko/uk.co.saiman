/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.maldi.stage.
 *
 * uk.co.saiman.maldi.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.stage.msapex;

import static uk.co.saiman.measurement.Units.metre;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.measure.Unit;
import javax.measure.quantity.Length;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import uk.co.saiman.instrument.stage.msapex.XYStageDiagram;
import uk.co.saiman.maldi.sampleplates.MaldiSampleWell;
import uk.co.saiman.maldi.stage.MaldiStage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class MaldiSampleWellDiagram extends XYStageDiagram {
  private final ObjectProperty<MaldiSampleWell> sampleWell;

  private final Rectangle measurementBounds = new Rectangle(-0.5, -0.5, 1, 1);

  private final Duration duration = Duration.millis(200);
  private final TranslateTransition translate = new TranslateTransition(
      duration,
      measurementBounds);
  private final ScaleTransition scale = new ScaleTransition(duration, measurementBounds);
  private final Animation measurementAnimation = new ParallelTransition(translate, scale);

  public MaldiSampleWellDiagram(MaldiStage stage) {
    this(stage, metre().micro().getUnit());
  }

  public MaldiSampleWellDiagram(MaldiStage stage, Unit<Length> unit) {
    super(stage, unit);

    this.sampleWell = new SimpleObjectProperty<>();
    this.sampleWell.addListener(plate -> updateSampleWell(this.sampleWell.get()));

    getAnnotationLayer()
        .measurementBoundsProperty()
        .bind(measurementBounds.boundsInParentProperty());
  }

  private void updateSampleWell(MaldiSampleWell sampleWell) {
    if (sampleWell != null) {
      var position = sampleWell.center().to(getUnit());
      var size = new XYCoordinate<>(sampleWell.radius(), sampleWell.radius())
          .to(getUnit())
          .multiply(2.5);

      translate.setToX(position.getXValue());
      translate.setToY(position.getYValue());

      scale.setToX(size.getXValue());
      scale.setToY(size.getYValue());

      measurementAnimation.play();

      try (var controller = getStageDevice().acquireControl(0, TimeUnit.MILLISECONDS)) {
        controller.requestAnalysis(position);
      } catch (TimeoutException | InterruptedException e) {
        // this is okay, it just means someone else has control of the stage
      }
    }
  }

  public ObjectProperty<MaldiSampleWell> sampleWellProperty() {
    return sampleWell;
  }

  public MaldiSampleWell getSampleWell() {
    return sampleWell.get();
  }

  public void setSampleWell(MaldiSampleWell value) {
    sampleWell.set(value);
  }
}