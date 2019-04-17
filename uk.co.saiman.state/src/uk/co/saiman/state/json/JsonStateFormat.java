/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.state.json;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.state.State;
import uk.co.saiman.state.StateList;
import uk.co.saiman.state.StateMap;
import uk.co.saiman.state.StateProperty;

abstract class JsonStateFormat<T extends State> implements TextFormat<T> {
  /*
   * Saving
   */

  State toState(Object value) {
    if (value instanceof String) {
      return new StateProperty((String) value);

    } else if (value instanceof JSONObject) {
      return toStateMap((JSONObject) value);

    } else if (value instanceof JSONArray) {
      return toStateList((JSONArray) value);
    }

    throw new IllegalArgumentException("Unexpected JSON element " + value);
  }

  StateList toStateList(JSONArray array) {
    StateList list = StateList.empty();
    for (Object element : array) {
      list = list.withAdded(toState(element));
    }
    return list;
  }

  StateMap toStateMap(JSONObject object) {
    StateMap map = StateMap.empty();
    for (String key : object.keySet()) {
      Object value = object.get(key);
      map = map.with(key, toState(value));
    }
    return map;
  }

  /*
   * Loading
   */

  JSONObject toJsonObject(StateMap map) {
    JSONObject object = new JSONObject();
    map.getKeys().forEach(key -> {
      object.put(key, toJson(map.get(key)));
    });
    return object;
  }

  JSONArray toJsonArray(StateList list) {
    JSONArray array = new JSONArray();
    list.stream().forEach(element -> {
      array.put(toJson(element));
    });
    return array;
  }

  Object toJson(State state) {
    switch (state.getKind()) {
    case MAP:
      return toJsonObject(state.asMap());
    case LIST:
      return toJsonArray(state.asList());
    case PROPERTY:
      return state.asProperty().getValue();
    }
    throw new IllegalArgumentException("Unexpected state element " + state);
  }
}
