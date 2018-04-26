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
 * This file is part of uk.co.saiman.comms.saint.
 *
 * uk.co.saiman.comms.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.saint.impl;

import java.util.Locale;

import org.osgi.util.converter.Converter;

import uk.co.saiman.comms.rest.ActionTableREST;
import uk.co.saiman.comms.rest.CommsREST;
import uk.co.saiman.comms.saint.SaintController;

public class SaintCommsREST implements CommsREST {
  private final SaintController controller;
  private final Converter converter;

  public SaintCommsREST(SaintController controller, Converter converter) {
    this.controller = controller;
    this.converter = converter;
  }

  @Override
  public String getName() {
    return "SAINT Comms";
  }

  @Override
  public String getID() {
    return SaintController.class.getName();
  }

  @Override
  public ActionTableREST getActions() {
    return new SaintControllerREST(getName(), controller, converter);
  }

  @Override
  public String getLocalisedText(String key, Locale locale) {
    return key;
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
