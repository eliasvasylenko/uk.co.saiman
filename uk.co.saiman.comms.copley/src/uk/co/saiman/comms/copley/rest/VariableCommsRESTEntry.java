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
 * This file is part of uk.co.saiman.comms.copley.
 *
 * uk.co.saiman.comms.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.rest;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;
import static uk.co.saiman.comms.copley.VariableBank.ACTIVE;
import static uk.co.saiman.comms.copley.VariableBank.STORED;
import static uk.co.saiman.comms.copley.rest.CopleyControllerREST.READ_VALUE;
import static uk.co.saiman.comms.copley.rest.CopleyControllerREST.SWITCH_BANK;
import static uk.co.saiman.comms.copley.rest.CopleyControllerREST.WRITE_VALUE;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.osgi.util.converter.Converter;
import org.osgi.util.converter.TypeReference;

import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.copley.BankedVariable;
import uk.co.saiman.comms.copley.CopleyAxis;
import uk.co.saiman.comms.copley.CopleyNode;
import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.Variable;
import uk.co.saiman.comms.copley.VariableBank;
import uk.co.saiman.comms.copley.WritableVariable;
import uk.co.saiman.comms.rest.ControllerRESTEntry;

public class VariableCommsRESTEntry implements ControllerRESTEntry {
  private static final String BANK_VALUE = "CURRENT_BANK";

  private final Map<String, Object> inputData = new LinkedHashMap<>();
  private final Map<String, Object> outputData = new LinkedHashMap<>();

  private final CopleyNode node;
  private final CopleyVariableID id;
  private final Converter converter;

  private final boolean writable;
  private final boolean banked;
  private VariableBank bank;

  public VariableCommsRESTEntry(CopleyNode node, CopleyVariableID id, Converter converter) {
    this.node = node;
    this.id = id;
    this.converter = converter;

    Variable<?> variable = node.getAxes().findAny().get().variable(id);
    this.writable = variable instanceof WritableVariable<?>;
    this.banked = variable instanceof BankedVariable<?>;
    bank = variable.getBank();

    readValue(true);
  }

  @Override
  public String getID() {
    return id.toString();
  }

  @Override
  public Stream<String> getActions() {
    return concat(
        of(READ_VALUE),
        concat(writable ? of(WRITE_VALUE) : empty(), banked ? of(SWITCH_BANK) : empty()));
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

    node.getAxes().forEach(axis -> {
      Object value = getVariable(axis).get();

      try {
        converter
            .convert(value)
            .to(new TypeReference<Map<String, String>>() {})
            .entrySet()
            .stream()
            .forEach(
                e -> inputData
                    .computeIfAbsent(e.getKey(), k -> new ArrayList<>())
                    .add(e.getValue()));
      } catch (Exception e) {
        throw new CommsException("Cannot convert " + value + " to map", e);
      }
    });

    if (updateOutput && writable) {
      outputData.clear();
      outputData.putAll(inputData);
    }

    this.inputData.clear();
    this.inputData.putAll(inputData);
    if (banked)
      this.inputData.put(BANK_VALUE, bank);
  }

  private Variable<?> getVariable(CopleyAxis axis) {
    Variable<?> variable = axis.variable(id);
    if (banked)
      variable = ((BankedVariable<?>) variable).forBank(bank);
    return variable;
  }

  @SuppressWarnings("unchecked")
  public void writeValue() {
    node.getAxes().forEach(axis -> {
      Variable<?> variable = getVariable(axis);
      Object value;

      Map<String, Object> axisOutput = outputData
          .entrySet()
          .stream()
          .collect(toMap(Entry::getKey, e -> ((List<?>) e.getValue()).get(variable.getAxis())));

      try {
        value = converter.convert(axisOutput).to(variable.getType());
      } catch (Exception e) {
        throw new CommsException(
            "Cannot convert output data map to " + variable.getType().getSimpleName(),
            e);
      }
      ((WritableVariable<Object>) variable).set(value);
    });
  }

  public void switchBank() {
    bank = bank == ACTIVE ? STORED : ACTIVE;
    readValue(true);
  }
}
