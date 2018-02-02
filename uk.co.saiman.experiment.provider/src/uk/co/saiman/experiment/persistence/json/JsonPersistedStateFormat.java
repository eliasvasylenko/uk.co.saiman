package uk.co.saiman.experiment.persistence.json;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.persistence.PersistedStateList;

@Component
public class JsonPersistedStateFormat implements TextFormat<PersistedState> {
  @Override
  public String getId() {
    return "uk.co.saiman.experiment.persistence.json";
  }

  @Override
  public String getExtension() {
    return "json";
  }

  @Override
  public String getMimeType() {
    return "application/json";
  }

  public void load(ReadableByteChannel inputChannel, PersistedState persistedState)
      throws IOException {
    JSONObject object = new JSONObject(loadString(inputChannel));
    persistedState.clear();
    fillMap(persistedState, object);
  }

  @Override
  public Payload<? extends PersistedState> decodeString(String string) {
    PersistedState persistedState = new PersistedState();
    decodeString(string, persistedState);
    return new Payload<>(persistedState);
  }

  public void decodeString(String string, PersistedState persistedState) {
    persistedState.clear();
    fillMap(persistedState, new JSONObject(string));
  }

  void fillMap(PersistedState persistedState, JSONObject object) {
    JSONArray names = object.names();
    if (names != null)
      names.forEach(n -> {
        String name = n.toString();
        Object value = object.get(name);

        if (value instanceof JSONArray) {
          fillMapList(persistedState.getMapList(name), (JSONArray) value);
        } else if (value instanceof JSONObject) {
          fillMap(persistedState.getMap(name), (JSONObject) value);
        } else {
          persistedState.forString(name).set(value.toString());
        }
      });
  }

  void fillMapList(PersistedStateList persistedStateList, JSONArray array) {
    array.forEach(v -> fillMap(persistedStateList.add(), (JSONObject) v));
  }

  @Override
  public String encodeString(Payload<? extends PersistedState> payload) {
    return buildMap(payload.data).toString(2);
  }

  JSONObject buildMap(PersistedState persistedState) {
    JSONObject object = new JSONObject();

    persistedState.getStrings().forEach(s -> object.put(s, persistedState.forString(s).get()));

    persistedState.getMaps().forEach(s -> object.put(s, buildMap(persistedState.getMap(s))));

    persistedState
        .getMapLists()
        .forEach(s -> object.put(s, buildMapList(persistedState.getMapList(s))));

    return object;
  }

  JSONArray buildMapList(PersistedStateList persistedStateList) {
    JSONArray array = new JSONArray();

    persistedStateList.forEach(l -> array.put(buildMap(l)));

    return array;
  }
}
