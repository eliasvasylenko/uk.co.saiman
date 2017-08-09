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
 * This file is part of uk.co.saiman.comms.
 *
 * uk.co.saiman.comms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.rest;

import java.util.Optional;
import java.util.stream.Stream;

/*
 * TODO? NOTE: Since this class does not actually rely on any other comms API, and
 * since the type of a {@link Comms#getControllerClass() comms controller} is
 * also unrelated to the comms API, it may be useful at some point to factor
 * this interface out into an independent bundle. This way, alternative
 * controller implementations may be provided that do not operate over serial
 * comms, and they would be able to share some of the same plumbing for
 * providing a REST interface.
 */
/**
 * A view of a comms device controller to adapt its functionality over a common
 * REST interface.
 * 
 * @author Elias N Vasylenko
 */
public interface ControllerREST {
  /**
   * @return a list of enumeration types the REST consumer should be aware of
   *         when presenting entry data
   */
  Stream<Class<? extends Enum<?>>> getEnums();

  Stream<ControllerRESTEntry> getEntries();

  default Optional<ControllerRESTEntry> getEntry(String entry) {
    return getEntries().filter(e -> e.getID().equals(entry)).findAny();
  }

  Stream<ControllerRESTAction> getActions();

  default Optional<ControllerRESTAction> getAction(String action) {
    return getActions().filter(a -> a.getID().equals(action)).findAny();
  }
}
