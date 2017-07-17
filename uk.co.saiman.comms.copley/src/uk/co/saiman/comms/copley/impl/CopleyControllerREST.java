package uk.co.saiman.comms.copley.impl;

import static java.util.stream.Stream.of;
import static uk.co.saiman.comms.rest.ControllerRESTAction.Behaviour.MODIFIES_OUTPUT_DATA;
import static uk.co.saiman.comms.rest.ControllerRESTAction.Behaviour.POLLABLE;
import static uk.co.saiman.comms.rest.ControllerRESTAction.Behaviour.RECEIVES_INPUT_DATA;
import static uk.co.saiman.comms.rest.ControllerRESTAction.Behaviour.SENDS_OUTPUT_DATA;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.copley.AmplifierMode;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.TrajectoryCommand;
import uk.co.saiman.comms.copley.TrajectoryProfileMode;
import uk.co.saiman.comms.copley.VariableBank;
import uk.co.saiman.comms.rest.ControllerREST;
import uk.co.saiman.comms.rest.ControllerRESTAction;
import uk.co.saiman.comms.rest.ControllerRESTEntry;
import uk.co.strangeskies.collection.stream.StreamUtilities;

public class CopleyControllerREST implements ControllerREST {
  static final String READ_VALUE = "readValue";
  static final String WRITE_VALUE = "writeValue";
  static final String SWITCH_BANK = "switchBank";

  private final Map<String, VariableCommsRESTEntry<?>> variableEntries;

  private final ControllerRESTAction readValue;
  private final ControllerRESTAction writeValue;
  private final ControllerRESTAction switchBank;

  public CopleyControllerREST(CopleyController controller, DTOs dtos) {
    this.variableEntries = new LinkedHashMap<>();
    Arrays.stream(CopleyVariableID.values()).map(controller::getVariable).forEach(
        variable -> variableEntries
            .put(variable.getID().toString(), new VariableCommsRESTEntry<>(variable, dtos)));

    readValue = new ControllerRESTAction() {
      @Override
      public void invoke(String entry) throws Exception {
        variableEntries.get(entry).readValue(false);
      }

      @Override
      public boolean hasBehaviour(Behaviour behaviour) {
        return behaviour == RECEIVES_INPUT_DATA || behaviour == POLLABLE;
      }

      @Override
      public String getID() {
        return READ_VALUE;
      }
    };
    writeValue = new ControllerRESTAction() {
      @Override
      public void invoke(String entry) throws Exception {
        variableEntries.get(entry).writeValue();
      }

      @Override
      public boolean hasBehaviour(Behaviour behaviour) {
        return behaviour == SENDS_OUTPUT_DATA;
      }

      @Override
      public String getID() {
        return WRITE_VALUE;
      }
    };
    switchBank = new ControllerRESTAction() {
      @Override
      public void invoke(String entry) throws Exception {
        variableEntries.get(entry).switchBank();
      }

      @Override
      public boolean hasBehaviour(Behaviour behaviour) {
        return behaviour == RECEIVES_INPUT_DATA || behaviour == MODIFIES_OUTPUT_DATA;
      }

      @Override
      public String getID() {
        return SWITCH_BANK;
      }
    };
  }

  @Override
  public Stream<Class<? extends Enum<?>>> getEnums() {
    return of(
        TrajectoryCommand.class,
        VariableBank.class,
        AmplifierMode.class,
        TrajectoryProfileMode.class);
  }

  @Override
  public Stream<ControllerRESTEntry> getEntries() {
    return StreamUtilities.upcastStream(variableEntries.values().stream());
  }

  @Override
  public Stream<ControllerRESTAction> getActions() {
    return of(readValue, writeValue, switchBank);
  }
}
