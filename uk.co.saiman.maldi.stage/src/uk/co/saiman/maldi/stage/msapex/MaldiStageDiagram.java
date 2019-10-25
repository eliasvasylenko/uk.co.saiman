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

import javax.measure.Unit;
import javax.measure.quantity.Length;

import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;

import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import uk.co.saiman.instrument.stage.msapex.XYStageDiagram;
import uk.co.saiman.maldi.stage.MaldiStage;

public class MaldiStageDiagram extends XYStageDiagram {
  private MPopupMenu popupMenu;

  public MaldiStageDiagram(MaldiStage stage, Image image) {
    this(stage, image, metre().micro().getUnit());
  }

  public MaldiStageDiagram(MaldiStage stage, Image image, Unit<Length> unit) {
    super(stage, unit);

    addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
      if (popupMenu != null) {
        ((ContextMenu) popupMenu.getWidget()).show(this, event.getScreenX(), event.getScreenY());
      }
      event.consume();
    });

    setImage(image);
  }

  @Override
  public MaldiStage getStageDevice() {
    return (MaldiStage) super.getStageDevice();
  }
}
