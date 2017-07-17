package uk.co.saiman.comms.copley.impl;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;
import static uk.co.saiman.comms.copley.impl.CopleyControllerREST.READ_VALUE;
import static uk.co.saiman.comms.copley.impl.CopleyControllerREST.SWITCH_BANK;
import static uk.co.saiman.comms.copley.impl.CopleyControllerREST.WRITE_VALUE;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.copley.BankedVariable;
import uk.co.saiman.comms.copley.Variable;
import uk.co.saiman.comms.copley.WritableVariable;
import uk.co.saiman.comms.rest.ControllerRESTEntry;

public class VariableCommsRESTEntry<U> implements ControllerRESTEntry {
  private static final String BANK_VALUE = "CURRENT_BANK";

  private final Map<String, Object> inputData = new LinkedHashMap<>();
  private final Map<String, Object> outputData = new LinkedHashMap<>();

  private Variable<U> variable;
  private final DTOs dtos;

  public VariableCommsRESTEntry(Variable<U> variable, DTOs dtos) {
    this.variable = variable;
    this.dtos = dtos;
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
            variable instanceof WritableVariable<?> ? of(WRITE_VALUE) : empty(),
            variable instanceof BankedVariable<?> ? of(SWITCH_BANK) : empty()));
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

    variable.getController().getAxes().forEach(axis -> {
      Object value = variable.get(axis);
      try {
        dtos.asMap(value).entrySet().stream().forEach(
            e -> inputData.computeIfAbsent(e.getKey(), k -> new ArrayList<>()).add(e.getValue()));
      } catch (Exception e) {
        throw new CommsException("Cannot convert " + value + " to map", e);
      }
    });

    if (updateOutput && variable instanceof WritableVariable<?>) {
      outputData.clear();
      outputData.putAll(inputData);
    }

    this.inputData.clear();
    this.inputData.putAll(inputData);
    if (variable instanceof BankedVariable<?>)
      this.inputData.put(BANK_VALUE, variable.getBank());
  }

  public void writeValue() {
    variable.getController().getAxes().forEach(axis -> {
      U value;

      Map<String, Object> axisOutput = outputData.entrySet().stream().collect(
          toMap(Entry::getKey, e -> ((List<?>) e.getValue()).get(axis.axisNumber())));

      try {
        value = dtos.convert(axisOutput).to(variable.getType());
      } catch (Exception e) {
        throw new CommsException(
            "Cannot convert output data map to " + variable.getType().getSimpleName(),
            e);
      }
      ((WritableVariable<U>) variable).set(axis, value);
    });
  }

  public void switchBank() {
    variable = ((BankedVariable<U>) variable).switchBank();
    readValue(true);
  }
}
