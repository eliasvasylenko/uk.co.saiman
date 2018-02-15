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
 * This file is part of uk.co.saiman.comms.provider.
 *
 * uk.co.saiman.comms.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.impl;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static uk.co.saiman.comms.CommsStatus.CLOSED;
import static uk.co.saiman.comms.rest.ControllerRESTAction.Behaviour.MODIFIES_OUTPUT_DATA;
import static uk.co.saiman.comms.rest.ControllerRESTAction.Behaviour.POLLABLE;
import static uk.co.saiman.comms.rest.ControllerRESTAction.Behaviour.RECEIVES_INPUT_DATA;
import static uk.co.saiman.comms.rest.ControllerRESTAction.Behaviour.SENDS_OUTPUT_DATA;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import osgi.enroute.dto.api.DTOs;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RequireRestImplementation;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.rest.CommsREST;
import uk.co.saiman.comms.rest.ActionTableREST;
import uk.co.saiman.comms.rest.ControllerRESTAction;
import uk.co.saiman.comms.rest.ControllerRESTEntry;

@RequireRestImplementation
@Component(property = REST.ENDPOINT + "=/api/comms/*")
public class CommsRESTProvider implements REST {
  private static final String NAME_KEY = "name";
  private static final String CONNECTION_KEY = "connection";
  private static final String STATUS_KEY = "status";
  private static final String STATUS_FAULT_KEY = "fault";
  private static final String CHANNEL_KEY = "channel";
  private static final String BUNDLE_KEY = "bundle";
  private static final String ENTRIES_KEY = "entries";
  private static final String ACTIONS_KEY = "actions";
  private static final String ENUMS_KEY = "enums";

  private static final String BUNDLE_SYMBOLIC_NAME_KEY = "symbolicName";
  private static final String BUNDLE_NAME_KEY = "name";
  private static final String BUNDLE_ID_KEY = "id";

  private static final String ENTRY_ID_KEY = "id";
  private static final String ENTRY_ACTIONS_KEY = "actions";
  private static final String ENTRY_INPUT_KEY = "input";
  private static final String ENTRY_OUTPUT_KEY = "output";

  private static final String ACTION_ID_KEY = "id";
  private static final String ACTION_POLLABLE_KEY = "pollable";
  private static final String ACTION_RECEIVES_INPUT_KEY = "receivesInput";
  private static final String ACTION_SENDS_OUTPUT_KEY = "sendsOutput";
  private static final String ACTION_MODIFIES_OUTPUT_KEY = "modifiesOutput";

  private static final String ERROR_KEY = "error";
  private static final String TRACE_KEY = "trace";

  private Map<CommsREST, Bundle> commsInterfaces;
  private Map<CommsREST, ActionTableREST> controllers;
  private ServiceTracker<CommsREST, CommsREST> commsInterfaceTracker;

  @Reference
  private DTOs dtos;

  @Activate
  void activate(BundleContext context) {
    commsInterfaces = new LinkedHashMap<>();
    controllers = new HashMap<>();

    commsInterfaceTracker = new ServiceTracker<>(
        context,
        CommsREST.class,
        new ServiceTrackerCustomizer<CommsREST, CommsREST>() {
          @Override
          public CommsREST addingService(ServiceReference<CommsREST> reference) {
            refreshCommsInterfaces(context);
            return context.getService(reference);
          }

          @Override
          public void modifiedService(ServiceReference<CommsREST> reference, CommsREST service) {
            refreshCommsInterfaces(context);
          }

          @Override
          public void removedService(ServiceReference<CommsREST> reference, CommsREST service) {
            refreshCommsInterfaces(context);
          }
        });
    commsInterfaceTracker.open();

    refreshCommsInterfaces(context);
  }

  @Deactivate
  void deactivate() {
    commsInterfaceTracker.close();
  }

  private synchronized void refreshCommsInterfaces(BundleContext context) {
    commsInterfaces.clear();
    try {
      for (ServiceReference<CommsREST> commsReference : context
          .getServiceReferences(CommsREST.class, null)) {
        commsInterfaces.put(context.getService(commsReference), commsReference.getBundle());
      }
    } catch (InvalidSyntaxException e) {
      throw new AssertionError();
    }
  }

  private CommsREST getNamedComms(String name) {
    return commsInterfaces
        .keySet()
        .stream()
        .filter(c -> c.getID().equals(name))
        .findAny()
        .orElseThrow(() -> new CommsException("Comms interface not found " + name));
  }

  public List<String> getAvailableComms() {
    return commsInterfaces.keySet().stream().map(CommsREST::getID).collect(toList());
  }

  public List<Map<String, Object>> getCommsInfo() {
    return commsInterfaces.keySet().stream().map(this::getCommsInfoImpl).collect(toList());
  }

  public Map<String, Object> getCommsInfo(String name) {
    return getCommsInfoImpl(getNamedComms(name));
  }

  public Map<String, Object> postOpenComms(String name) {
    CommsREST comms = getNamedComms(name);
    comms.openController();
    getController(comms);
    return getConnectionInfo(comms);
  }

  private ActionTableREST getController(CommsREST comms) {
    if (comms.getStatus() == CLOSED)
      throw new IllegalStateException(
          "Cannot access controller while comms is closed " + comms.getName());

    controllers.put(comms, comms.openController());
    return controllers.get(comms);
  }

  public Map<String, Object> postResetComms(String name) {
    CommsREST comms = getNamedComms(name);
    comms.reset();
    return getConnectionInfo(comms);
  }

  private Map<String, Object> getCommsInfoImpl(CommsREST comms) {
    Map<String, Object> info = new HashMap<>();

    info.put(NAME_KEY, comms.getName());
    info.put(CONNECTION_KEY, getConnectionInfo(comms));
    info.put(BUNDLE_KEY, getBundleInfoImpl(commsInterfaces.get(comms)));

    return info;
  }

  public Map<String, Object> getControllerInfo(String commsName) {
    return getControllerInfoImpl(getController(getNamedComms(commsName)));
  }

  private Map<String, Object> getControllerInfoImpl(ActionTableREST controller) {
    Map<String, Object> info = new HashMap<>();

    info
        .put(
            ENTRIES_KEY,
            controller.getEntries().map(ControllerRESTEntry::getID).collect(toList()));
    info
        .put(
            ACTIONS_KEY,
            controller.getActions().map(ControllerRESTAction::getID).collect(toList()));
    info
        .put(
            ENUMS_KEY,
            controller
                .getEnums()
                .collect(
                    toMap(
                        Class::getName,
                        e -> stream(e.getEnumConstants()).map(Enum::name).collect(toList()))));

    return info;
  }

  private Map<String, Object> getConnectionInfo(CommsREST comms) {
    Map<String, Object> info = new HashMap<>();

    info.put(STATUS_KEY, comms.getStatus());
    info.put(CHANNEL_KEY, comms.getPort());
    comms.getFaultText().ifPresent(fault -> info.put(STATUS_FAULT_KEY, fault));

    return info;
  }

  private Object getBundleInfoImpl(Bundle bundle) {
    Map<String, Object> info = new HashMap<>();

    info.put(BUNDLE_NAME_KEY, bundle.getHeaders().get(Constants.BUNDLE_NAME));
    info.put(BUNDLE_SYMBOLIC_NAME_KEY, bundle.getSymbolicName());
    info.put(BUNDLE_ID_KEY, bundle.getBundleId());

    return info;
  }

  public Map<String, Map<String, Object>> getEntriesInfo(String commsName) {
    CommsREST comms = getNamedComms(commsName);
    return getController(comms)
        .getEntries()
        .collect(toMap(c -> c.getID(), c -> getEntryInfoImpl(c)));
  }

  private Map<String, Object> getEntryInfoImpl(ControllerRESTEntry entry) {
    Map<String, Object> info = new HashMap<>();

    info.put(ENTRY_ID_KEY, entry.getID());
    info.put(ENTRY_OUTPUT_KEY, entry.getOutputData());
    info.put(ENTRY_ACTIONS_KEY, entry.getActions().collect(toList()));

    return info;
  }

  public Map<String, Map<String, Object>> getActionsInfo(String commsName) {
    CommsREST comms = getNamedComms(commsName);
    return getController(comms)
        .getActions()
        .collect(toMap(c -> c.getID(), c -> getActionInfoImpl(c)));
  }

  private Map<String, Object> getActionInfoImpl(ControllerRESTAction action) {
    Map<String, Object> info = new HashMap<>();

    info.put(ACTION_ID_KEY, action.getID());
    info.put(ACTION_POLLABLE_KEY, action.hasBehaviour(POLLABLE));
    info.put(ACTION_RECEIVES_INPUT_KEY, action.hasBehaviour(RECEIVES_INPUT_DATA));
    info.put(ACTION_SENDS_OUTPUT_KEY, action.hasBehaviour(SENDS_OUTPUT_DATA));
    info.put(ACTION_MODIFIES_OUTPUT_KEY, action.hasBehaviour(MODIFIES_OUTPUT_DATA));

    return info;
  }

  public Map<String, Object> postActionInvocation(
      Map<String, Object> output,
      String commsID,
      String entryID,
      String actionID) {
    CommsREST comms = getNamedComms(commsID);
    ControllerRESTEntry entry = getController(comms).getEntry(entryID).get();
    ControllerRESTAction action = getController(comms).getAction(actionID).get();

    try {
      if (action.hasBehaviour(SENDS_OUTPUT_DATA))
        entry.getOutputData().replaceAll((key, value) -> output.get(key));

      action.invoke(entryID);

      Map<String, Object> result = new HashMap<>();
      if (action.hasBehaviour(RECEIVES_INPUT_DATA))
        result.put(ENTRY_INPUT_KEY, entry.getInputData());
      if (action.hasBehaviour(MODIFIES_OUTPUT_DATA))
        result.put(ENTRY_OUTPUT_KEY, entry.getOutputData());

      return result;
    } catch (Exception e) {
      Map<String, Object> errorMap = new HashMap<>();

      String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
      errorMap.put(ERROR_KEY, message);

      StringWriter writer = new StringWriter();
      e.printStackTrace(new PrintWriter(writer));
      errorMap.put(TRACE_KEY, writer.toString());

      return errorMap;
    }
  }
}
