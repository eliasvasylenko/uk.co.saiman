package uk.co.saiman.comms.copley.impl;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.MODIFIES_OUTPUT_DATA;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.POLLABLE;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.RECEIVES_INPUT_DATA;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.SENDS_OUTPUT_DATA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.copley.AmplifierMode;
import uk.co.saiman.comms.copley.BankedVariable;
import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.TrajectoryCommand;
import uk.co.saiman.comms.copley.TrajectoryProfileMode;
import uk.co.saiman.comms.copley.Variable;
import uk.co.saiman.comms.copley.VariableBank;
import uk.co.saiman.comms.copley.WritableVariable;
import uk.co.saiman.comms.rest.CommsRESTAction;
import uk.co.saiman.comms.rest.CommsRESTEntry;
import uk.co.saiman.comms.rest.SimpleCommsREST;
import uk.co.strangeskies.collection.stream.StreamUtilities;

public class CopleyCommsREST<T extends Enum<T>> extends SimpleCommsREST<CopleyComms<T>> {
  private static final String READ_VALUE = "readValue";
  private static final String WRITE_VALUE = "writeValue";
  private static final String SWITCH_BANK = "switchBank";

  private final DTOs dtos;

  private final Map<String, VariableCommsRESTEntry<?>> variableEntries;

  private final CommsRESTAction readValue;
  private final CommsRESTAction writeValue;
  private final CommsRESTAction switchBank;

  public CopleyCommsREST(CopleyComms<T> comms, DTOs dtos) {
    super(comms);
    this.dtos = dtos;

    this.variableEntries = new LinkedHashMap<>();
    Arrays.stream(CopleyVariableID.values()).map(comms::getVariable).forEach(
        variable -> variableEntries
            .put(variable.getID().toString(), new VariableCommsRESTEntry<>(variable)));

    readValue = new CommsRESTAction() {
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
    writeValue = new CommsRESTAction() {
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
    switchBank = new CommsRESTAction() {
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
  public Stream<CommsRESTEntry> getEntries() {
    return StreamUtilities.upcastStream(variableEntries.values().stream());
  }

  @Override
  public Stream<CommsRESTAction> getActions() {
    return of(readValue, writeValue, switchBank);
  }

  @Override
  public String getLocalisedText(String key, Locale locale) {
    return key;
  }

  @Override
  public void open() {
    super.open();
    variableEntries.values().stream().forEach(v -> v.readValue(true));
  }

  public class VariableCommsRESTEntry<U> implements CommsRESTEntry {
    private static final String BANK_VALUE = "CURRENT_BANK";

    private final Map<String, Object> inputData = new LinkedHashMap<>();
    private final Map<String, Object> outputData = new LinkedHashMap<>();

    private Variable<T, U> variable;

    public VariableCommsRESTEntry(Variable<T, U> variable) {
      this.variable = variable;
    }

    @Override
    public String getID() {
      return variable.getID().toString();
    }

    @Override
    public Stream<String> getActions() {
      return concat(
          of(READ_VALUE),
          concat(
              variable instanceof WritableVariable<?, ?> ? of(WRITE_VALUE) : empty(),
              variable instanceof BankedVariable<?, ?> ? of(SWITCH_BANK) : empty()));
    }

    @Override
    public Map<String, Object> getInputData() {
      return new LinkedHashMap<>(inputData);
    }

    @Override
    public Map<String, Object> getOutputData() {
      return outputData;
    }

    public void readValue(boolean updateOutput) {
      Map<String, List<Object>> inputData = new LinkedHashMap<>();

      getComms().getAxes().forEach(axis -> {
        Object value = variable.get(axis);
        try {
          dtos.asMap(value).entrySet().stream().forEach(
              e -> inputData.computeIfAbsent(e.getKey(), k -> new ArrayList<>()).add(e.getValue()));
        } catch (Exception e) {
          throw new CommsException("Cannot convert " + value + " to map", e);
        }
      });

      if (updateOutput && variable instanceof WritableVariable<?, ?>) {
        outputData.clear();
        outputData.putAll(inputData);
      }

      this.inputData.clear();
      this.inputData.putAll(inputData);
      if (variable instanceof BankedVariable<?, ?>)
        this.inputData.put(BANK_VALUE, variable.getBank());
    }

    public void writeValue() {
      getComms().getAxes().forEach(axis -> {
        U value;

        Map<String, Object> axisOutput = outputData.entrySet().stream().collect(
            toMap(Entry::getKey, e -> ((List<?>) e.getValue()).get(axis.ordinal())));

        try {
          value = dtos.convert(axisOutput).to(variable.getType());
        } catch (Exception e) {
          throw new CommsException(
              "Cannot convert output data map to " + variable.getType().getSimpleName(),
              e);
        }
        ((WritableVariable<T, U>) variable).set(axis, value);
      });
    }

    public void switchBank() {
      variable = ((BankedVariable<T, U>) variable).switchBank();
      readValue(true);
    }
  }
}
