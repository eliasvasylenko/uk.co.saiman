/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.msapex.instrument.
 *
 * uk.co.saiman.msapex.instrument is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.instrument;

import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.fx.core.di.LocalInstance;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.eclipse.treeview.ModularTreeController;
import uk.co.saiman.instrument.Instrument;

/**
 * Experiment management view part. Manage experiments and their results in the
 * experiment tree.
 * 
 * @author Elias N Vasylenko
 */
public class InstrumentPart {
  static final String OSGI_SERVICE = "osgi.service";

  @FXML
  private ModularTreeController<Instrument> modularTreeController;

  @Inject
  private Instrument instrument;

  @PostConstruct
  void initialize(BorderPane container, @LocalInstance FXMLLoader loader) {
    container.setCenter(buildWith(loader).controller(this).loadRoot());

    modularTreeController.setRootData(instrument);
  }

  public ModularTreeController<Instrument> getExperimentTreeController() {
    return modularTreeController;
  }
}
