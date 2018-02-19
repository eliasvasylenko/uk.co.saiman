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
 * This file is part of uk.co.saiman.comms.rest.
 *
 * uk.co.saiman.comms.rest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.rest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.rest;

import java.util.Locale;

/**
 * A view of a comms device to adapt its functionality over a common REST
 * interface.
 * 
 * @author Elias N Vasylenko
 */
public interface CommsREST {
  /**
   * @return the unique ID of the device
   */
  String getID();

  /**
   * @return the human-readable name of the device
   */
  String getName();

  /**
   * Open the comms device
   */
  ActionTableREST getActions();

  /**
   * Reset the comms device
   */
  void reset();

  String getPort();

  String getLocalisedText(String key, Locale locale);
}
