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
package uk.co.saiman.comms.copley.impl;

import java.util.Locale;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.rest.ControllerREST;
import uk.co.saiman.comms.rest.SimpleCommsREST;

public class CopleyCommsREST extends SimpleCommsREST<CopleyComms, CopleyController> {
  private final DTOs dtos;

  public CopleyCommsREST(CopleyComms comms, DTOs dtos) {
    super(comms);
    this.dtos = dtos;
  }

  @Override
  public String getLocalisedText(String key, Locale locale) {
    return key;
  }

  @Override
  public ControllerREST createControllerREST(CopleyController controller) {
    return new CopleyControllerREST(controller, dtos);
  }
}
