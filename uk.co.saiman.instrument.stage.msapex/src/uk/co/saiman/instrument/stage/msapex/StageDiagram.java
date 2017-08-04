/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.instrument.stage.
 *
 * uk.co.saiman.instrument.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.msapex;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import javax.measure.Quantity;

import javafx.scene.image.Image;
import uk.co.saiman.experiment.sample.StageConfiguration;
import uk.co.saiman.instrument.stage.StageDevice;

/*-
 * 
 * Options:
 * 
 * - ONE stage diagram available per stage (highest rank is chosen, default provided with low rank)
 * - Stage device wired to a diagram via OSGi
 * 
 *     pros:
 *       straightforward to get the diagram for a stage
 *     cons:
 *       custom UI for selecting e.g. different MALDI plates
 * 
 */
public abstract class StageDiagram {
  private StageDevice stageDevice;
  private Image image;
  private StageConfiguration stageConfiguration;

  public StageDevice getStageDevice() {
    return stageDevice;
  }

  public Image getImage() {
    return image;
  }

  protected void setImage(Image image) {
    this.image = image;
  }

  public Optional<StageConfiguration> getStageConfiguration() {
    return Optional.ofNullable(stageConfiguration);
  }

  protected void setStageConfiguration(StageConfiguration stageConfiguration) {
    this.stageConfiguration = stageConfiguration;
  }

  abstract Stream<Quantity<?>> getCoordinatesAtPixel(int pixelX, int pixelY);

  abstract int getPixelXAtCoordinates(Collection<? extends Quantity<?>> coordinates);

  abstract int getPixelYAtCoordinates(Collection<? extends Quantity<?>> coordinates);

  abstract Stream<? extends StageDiagramSampleConfiguration> getSampleConfigurations();
}
