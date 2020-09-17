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
