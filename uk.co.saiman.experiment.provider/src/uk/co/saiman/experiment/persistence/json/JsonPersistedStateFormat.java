package uk.co.saiman.experiment.persistence.json;

import static java.nio.channels.Channels.newInputStream;
import static java.nio.channels.Channels.newOutputStream;
import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.persistence.PersistedStateList;

@Component
public class JsonPersistedStateFormat implements DataFormat<PersistedState> {
  @Override
  public String getId() {
    return "uk.co.saiman.experiment.persistence.json";
  }

  @Override
  public String getExtension() {
    return "json";
  }

  @Override
  public Payload<? extends PersistedState> load(ReadableByteChannel inputChannel)
      throws IOException {
    PersistedState persistedState = new PersistedState();
    load(inputChannel, persistedState);
    return new Payload<>(persistedState);
  }

  public void load(ReadableByteChannel inputChannel, PersistedState persistedState)
      throws IOException {
    String string = new BufferedReader(new InputStreamReader(newInputStream(inputChannel)))
        .lines()
        .collect(joining());
    JSONObject object = new JSONObject(string);
    persistedState.clear();
    fillMap(persistedState, object);
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
  public void save(WritableByteChannel outputChannel, Payload<? extends PersistedState> payload)
      throws IOException {
    JSONObject object = buildMap(payload.data);
    try (PrintWriter out = new PrintWriter(newOutputStream(outputChannel))) {
      out.println(object.toString(2));
    }
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
