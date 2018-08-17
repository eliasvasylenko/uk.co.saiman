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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.state;

import static uk.co.saiman.data.format.MediaType.APPLICATION_TYPE;
import static uk.co.saiman.data.format.RegistrationTree.VENDOR;

import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;

@Component
public class JsonStateMapFormat implements TextFormat<StateMap> {
  public static final int VERSION = 1;

  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.statemap.v" + VERSION,
      VENDOR).withSuffix("json");

  @Override
  public String getExtension() {
    return "jsm";
  }

  @Override
  public Stream<MediaType> getMediaTypes() {
    return Stream.of(MEDIA_TYPE);
  }

  @Override
  public Payload<? extends StateMap> decodeString(String string) {
    JSONObject object = new JSONObject(new JSONTokener(string));
    return new Payload<>(toStateMap(object));
  }

  private State toState(Object value) {
    if (value instanceof String) {
      return (StateProperty) () -> (String) value;

    } else if (value instanceof JSONObject) {
      return toStateMap((JSONObject) value);

    } else if (value instanceof JSONArray) {
      return toStateList((JSONArray) value);
    }

    throw new IllegalArgumentException("Unexpected JSON element " + value);
  }

  private StateList toStateList(JSONArray array) {
    StateList list = StateList.empty();
    for (Object element : array) {
      list = list.withAdded(toState(element));
    }
    return list;
  }

  private StateMap toStateMap(JSONObject object) {
    StateMap map = StateMap.empty();
    for (String key : object.keySet()) {
      Object value = object.get(key);
      map = map.with(key, toState(value));
    }
    return map;
  }

  @Override
  public String encodeString(Payload<? extends StateMap> payload) {
    return toJsonObject(payload.data).toString(2);
  }

  private JSONObject toJsonObject(StateMap map) {
    JSONObject object = new JSONObject();
    map.getKeys().forEach(key -> {
      object.put(key, toJson(map.get(key)));
    });
    return object;
  }

  private JSONArray toJsonArray(StateList list) {
    JSONArray array = new JSONArray();
    list.stream().forEach(element -> {
      array.put(toJson(element));
    });
    return array;
  }

  private Object toJson(State state) {
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
