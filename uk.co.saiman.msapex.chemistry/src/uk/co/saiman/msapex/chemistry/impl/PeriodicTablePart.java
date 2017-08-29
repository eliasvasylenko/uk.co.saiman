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
 * This file is part of uk.co.saiman.msapex.chemistry.
 *
 * uk.co.saiman.msapex.chemistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.chemistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.chemistry.impl;

import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.chemistry.Element;
import uk.co.saiman.chemistry.PeriodicTable;

/**
 * An Eclipse part for display of a periodic table.
 * 
 * @author Elias N Vasylenko
 */
public class PeriodicTablePart {
  @Inject
  @Optional
  PeriodicTable periodicTable;

  @FXML
  private PeriodicTableController periodicTableController;

  @FXML
  private ChemicalElementPanelController chemicalElementPanelController;

  @FXML
  private ScrollPane periodicTableScrollPane;

  @PostConstruct
  void initialize(BorderPane container, @LocalInstance FXMLLoader loader) {
    container.setCenter(buildWith(loader).controller(this).loadRoot());
    periodicTableController.weakReference(this).observe(m -> m.owner().setElementTile(m.message()));
    periodicTableController.setTilesFocusTraversable(true);
    setPeriodicTable(periodicTable);
  }

  protected void setPeriodicTable(PeriodicTable table) {
    periodicTableController.setPeriodicTable(table);
    if (table != null) {
      chemicalElementPanelController.setElement(table.getElement(1));
    } else {
      chemicalElementPanelController.setElement(null);
    }
  }

  /**
   * @return The controller of the periodic table UI component
   */
  public PeriodicTableController getPeriodicTableController() {
    return periodicTableController;
  }

  /**
   * Send a click event to the tile.
   * 
   * @param event
   *          The mouse event to apply to this tile
   */
  public void onMousePressed(MouseEvent event) {
    periodicTableScrollPane.requestFocus();
    event.consume();
  }

  private void setElementTile(ChemicalElementTile tile) {
    Element element = tile.getElement();

    double hPadding = 2;
    double vPadding = 2;

    chemicalElementPanelController.setElement(element);

    Bounds contentBounds = periodicTableScrollPane.getContent().getBoundsInLocal();
    Bounds viewportBounds = periodicTableScrollPane.getViewportBounds();

    Bounds location = tile.getBoundsInLocal();
    Node parent = tile;
    do {
      location = parent.getLocalToParentTransform().transform(location);
      parent = parent.getParent();
    } while (parent != periodicTableScrollPane.getContent());

    double scrollableDistanceH = contentBounds.getWidth() - viewportBounds.getWidth();
    double scrollableDistanceV = contentBounds.getHeight() - viewportBounds.getHeight();

    viewportBounds = new BoundingBox(
        scrollableDistanceH * periodicTableScrollPane.getHvalue(),
        scrollableDistanceV * periodicTableScrollPane.getVvalue(),
        viewportBounds.getWidth(),
        viewportBounds.getHeight());

    double scrollPosition;

    if (location.getMinX() - hPadding < viewportBounds.getMinX()) {
      scrollPosition = (location.getMinX() - hPadding);
      periodicTableScrollPane.setHvalue(scrollPosition / scrollableDistanceH);

    } else if (location.getMaxX() > viewportBounds.getMaxX()) {
      scrollPosition = (location.getMaxX() + hPadding - viewportBounds.getWidth());
      periodicTableScrollPane.setHvalue(scrollPosition / scrollableDistanceH);
    }

    if (location.getMinY() < viewportBounds.getMinY()) {
      scrollPosition = (location.getMinY() - vPadding);
      periodicTableScrollPane.setVvalue(scrollPosition / scrollableDistanceV);

    } else if (location.getMaxY() > viewportBounds.getMaxY()) {
      scrollPosition = (location.getMaxY() + vPadding - viewportBounds.getHeight());
      periodicTableScrollPane.setVvalue(scrollPosition / scrollableDistanceV);
    }
  }
}
