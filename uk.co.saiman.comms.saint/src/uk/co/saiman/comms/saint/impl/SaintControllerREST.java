package uk.co.saiman.comms.saint.impl;

import static java.util.stream.Stream.of;
import static uk.co.saiman.comms.rest.ControllerRESTAction.Behaviour.MODIFIES_OUTPUT_DATA;
import static uk.co.saiman.comms.rest.ControllerRESTAction.Behaviour.POLLABLE;
import static uk.co.saiman.comms.rest.ControllerRESTAction.Behaviour.RECEIVES_INPUT_DATA;
import static uk.co.saiman.comms.rest.ControllerRESTAction.Behaviour.SENDS_OUTPUT_DATA;
import static uk.co.strangeskies.collection.stream.StreamUtilities.upcastStream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.rest.ControllerREST;
import uk.co.saiman.comms.rest.ControllerRESTAction;
import uk.co.saiman.comms.rest.ControllerRESTEntry;
import uk.co.saiman.comms.saint.SaintController;
import uk.co.saiman.comms.saint.Value;
import uk.co.saiman.comms.saint.ValueReadback;
import uk.co.saiman.comms.saint.ValueRequest;

final class SaintControllerREST implements ControllerREST {
  static final String GET_ACTUAL_VALUE = "getActual";
  static final String SET_REQUESTED_VALUE = "setRequested";
  static final String GET_REQUESTED_VALUE = "getRequested";

  private final ControllerRESTAction getActual;
  private final ControllerRESTAction setRequested;
  private final ControllerRESTAction getRequested;
  private final Map<String, SAINTCommsRESTEntry> namedEntries = new LinkedHashMap<>();

  SaintControllerREST(String name, SaintController controller, DTOs dtos) {
    /*
     * It's a bit naff to use reflection for this, but it keeps the API tidy and
     * makes evolution easier so sue me
     */

    Set<SAINTCommsRESTEntry> entries = new TreeSet<>(
        Comparator.comparing(
            e -> 0xFF & (e.request != null
                ? e.request.getRequestedValueAddress()
                : e.readback.getActualValueAddress()).getBytes()[0]));

    try {
      for (Method method : SaintController.class.getDeclaredMethods()) {
        String methodName = method.getName();

        if (method.getReturnType() == Value.class) {
          Value<?> value = (Value<?>) method.invoke(controller);
          entries.add(new SAINTCommsRESTEntry(methodName, value, value, dtos));

        } else if (method.getReturnType() == ValueReadback.class) {
          entries.add(
              new SAINTCommsRESTEntry(
                  methodName,
                  (ValueReadback<?>) method.invoke(controller),
                  null,
                  dtos));

        } else if (method.getReturnType() == ValueRequest.class) {
          entries.add(
              new SAINTCommsRESTEntry(
                  methodName,
                  null,
                  (ValueRequest<?>) method.invoke(controller),
                  dtos));
        }
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new CommsException("Problem initialising REST interface for " + name, e);
    }

    entries.forEach(e -> namedEntries.put(e.getID(), e));

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

    namedEntries.values().stream().filter(e -> e.request != null).forEach(
        SAINTCommsRESTEntry::getRequestedValue);
  }

  @Override
  public Stream<Class<? extends Enum<?>>> getEnums() {
    return Stream.empty();
  }

  @Override
  public Stream<ControllerRESTEntry> getEntries() {
    return upcastStream(namedEntries.values().stream());
  }

  @Override
  public Stream<ControllerRESTAction> getActions() {
    return of(getRequested, setRequested, getActual);
  }
}
