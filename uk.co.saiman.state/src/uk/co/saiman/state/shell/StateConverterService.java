/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.state.
 *
 * uk.co.saiman.state is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.state is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.state.shell;

import org.apache.felix.service.command.Converter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.format.Payload;
import uk.co.saiman.state.State;
import uk.co.saiman.state.StateList;
import uk.co.saiman.state.StateMap;
import uk.co.saiman.state.json.JsonStateListFormat;
import uk.co.saiman.state.json.JsonStateMapFormat;

@Component
public class StateConverterService implements Converter {
  private final JsonStateMapFormat mapFormat;
  private final JsonStateListFormat listFormat;

  @Activate
  public StateConverterService() {
    mapFormat = new JsonStateMapFormat();
    listFormat = new JsonStateListFormat();
  }

  @Override
  public Object convert(Class<?> desiredType, Object in) throws Exception {
    if (!(in instanceof CharSequence)) {
      return null;
    }

    if (desiredType.isAssignableFrom(StateMap.class)) {
      return mapFormat.decodeString(in.toString()).data;

    } else if (desiredType.isAssignableFrom(StateList.class)) {
      return listFormat.decodeString(in.toString()).data;

    } else if (desiredType.isAssignableFrom(State.class)) {
      try {
        return mapFormat.decodeString(in.toString()).data;
      } catch (Exception e1) {
        try {
          return listFormat.decodeString(in.toString()).data;
        } catch (Exception e2) {}
      }

    }

    return null;
  }

  @Override
  public CharSequence format(Object target, int level, Converter escape) throws Exception {
    if (target instanceof StateMap) {
      return mapFormat.encodeString(new Payload<>((StateMap) target));

    } else if (target instanceof StateList) {
      return listFormat.encodeString(new Payload<>((StateList) target));
    }

    return null;
  }

}
