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

import static java.util.Optional.ofNullable;

import java.util.Optional;

import uk.co.saiman.comms.Comms;
import uk.co.saiman.comms.Comms.CommsStatus;
import uk.co.saiman.comms.CommsException;

public abstract class SimpleCommsREST<U extends Comms<T>, T> implements CommsREST {
  private final U comms;
  private T controller;
  private ControllerREST controllerREST;
  private CommsException lastFault;

  public SimpleCommsREST(U comms) {
    this.comms = comms;
  }

  protected U getComms() {
    return comms;
  }

  @Override
  public String getID() {
    return (getCategoryName() + "-" + comms.getPort().getName())
        .replace(' ', '-')
        .replace('/', '-');
  }

  @Override
  public String getName() {
    return getCategoryName() + " " + comms.getPort().getName();
  }

  public abstract String getCategoryName();

  @Override
  public CommsStatus getStatus() {
    return comms.status().get();
  }

  @Override
  public String getPort() {
    return comms.getPort().getName();
  }

  @Override
  public Optional<String> getFaultText() {
    return ofNullable(lastFault).map(CommsException::getMessage);
  }

  @Override
  public ControllerREST openController() {
    T controller = comms.openController();
    if (this.controller != controller) {
      this.controller = controller;
      controllerREST = createControllerREST(controller);
    }
    return controllerREST;
  }

  public abstract ControllerREST createControllerREST(T controller);

  @Override
  public void reset() {
    comms.reset();
  }
}
