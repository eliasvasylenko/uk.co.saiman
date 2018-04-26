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
 * This file is part of uk.co.saiman.comms.copley.
 *
 * uk.co.saiman.comms.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.rest;

import java.util.Locale;

import org.osgi.util.converter.Converter;

import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.rest.ActionTableREST;
import uk.co.saiman.comms.rest.CommsREST;

public class CopleyCommsREST implements CommsREST {
  private final CopleyController controller;
  private final Converter converter;

  public CopleyCommsREST(CopleyController controller, Converter converter) {
    this.controller = controller;
    this.converter = converter;
  }

  @Override
  public String getLocalisedText(String key, Locale locale) {
    return key;
  }

  @Override
  public String getID() {
    return CopleyController.class.getName();
  }

  @Override
  public String getName() {
    return "Copley Comms";
  }

  @Override
  public ActionTableREST getActions() {
    return new CopleyControllerREST(controller, converter);
  }

  @Override
  public void reset() {
    controller.reset();
  }

  @Override
  public String getPort() {
    return controller.getPort().toString();
  }
}
