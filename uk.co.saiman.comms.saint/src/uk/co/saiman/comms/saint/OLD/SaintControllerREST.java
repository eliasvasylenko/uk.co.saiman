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
package uk.co.saiman.comms.saint.OLD;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.util.converter.Converter;

import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.saint.SaintController;
import uk.co.saiman.comms.saint.Value;
import uk.co.saiman.comms.saint.ValueReadback;
import uk.co.saiman.comms.saint.ValueRequest;

final class SaintControllerREST {// TODO implements ActionTableREST {
  static final String GET_ACTUAL_VALUE = "getActual";
  static final String SET_REQUESTED_VALUE = "setRequested";
  static final String GET_REQUESTED_VALUE = "getRequested";

  // private final ControllerRESTAction getActual;
  // private final ControllerRESTAction setRequested;
  // private final ControllerRESTAction getRequested;
  private final Map<String, SAINTCommsRESTEntry> namedEntries = new LinkedHashMap<>();

  SaintControllerREST(String name, SaintController controller, Converter converter) {
    /*
     * It's a bit naff to use reflection for this, but it keeps the API tidy and
     * makes evolution easier so sue me
     */

    Set<SAINTCommsRESTEntry> entries = new TreeSet<>(
        Comparator
            .comparing(
                e -> 0xFF
                    & (e.request != null
                        ? e.request.getRequestedValueAddress()
                        : e.readback.getActualValueAddress()).getBytes()[0]));

    try {
      for (Method method : SaintController.class.getDeclaredMethods()) {
        String methodName = method.getName();

        if (method.getReturnType() == Value.class) {
          Value<?> value = (Value<?>) method.invoke(controller);
          entries.add(new SAINTCommsRESTEntry(methodName, value, value, converter));

        } else if (method.getReturnType() == ValueReadback.class) {
          entries
              .add(
                  new SAINTCommsRESTEntry(
                      methodName,
                      (ValueReadback<?>) method.invoke(controller),
                      null,
                      converter));

        } else if (method.getReturnType() == ValueRequest.class) {
          entries
              .add(
                  new SAINTCommsRESTEntry(
                      methodName,
                      null,
                      (ValueRequest<?>) method.invoke(controller),
                      converter));
        }
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new CommsException("Problem initialising REST interface for " + name, e);
    }

    entries.forEach(e -> namedEntries.put(e.getID(), e));

    /*- TODO
    getActual = new ControllerRESTAction() {
      @Override
      public boolean hasBehaviour(Behaviour behaviour) {
        return behaviour == POLLABLE || behaviour == RECEIVES_INPUT_DATA;
      }

      @Override
      public void invoke(String entry) throws Exception {
        namedEntries.get(entry).getActualValue();
      }

      @Override
      public String getID() {
        return GET_ACTUAL_VALUE;
      }
    };
    setRequested = new ControllerRESTAction() {
      @Override
      public boolean hasBehaviour(Behaviour behaviour) {
        return behaviour == SENDS_OUTPUT_DATA;
      }

      @Override
      public void invoke(String entry) throws Exception {
        namedEntries.get(entry).setRequestedValue();
      }

      @Override
      public String getID() {
        return SET_REQUESTED_VALUE;
      }
    };
    getRequested = new ControllerRESTAction() {
      @Override
      public boolean hasBehaviour(Behaviour behaviour) {
        return behaviour == MODIFIES_OUTPUT_DATA;
      }

      @Override
      public void invoke(String entry) throws Exception {
        namedEntries.get(entry).getRequestedValue();
      }

      @Override
      public String getID() {
        return GET_REQUESTED_VALUE;
      }
    };
     */
    namedEntries
        .values()
        .stream()
        .filter(e -> e.request != null)
        .forEach(SAINTCommsRESTEntry::getRequestedValue);
  }
}
