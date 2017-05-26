/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.comms.
 *
 * uk.co.saiman.comms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import osgi.enroute.dto.api.DTOs;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RequireRestImplementation;
import uk.co.saiman.comms.Command;
import uk.co.saiman.comms.Comms;
import uk.co.saiman.comms.CommsException;

@RequireRestImplementation
@Component(property = REST.ENDPOINT + "=/api/comms/*")
public class CommsRESTProvider implements REST {
	private class CommsRegistration {
		private final Comms<?> comms;
		private final Bundle bundle;

		public CommsRegistration(Comms<?> comms, Bundle bundle) {
			this.comms = comms;
			this.bundle = bundle;
		}

		public Comms<?> comms() {
			return comms;
		}

		public String uid() {
			return (comms.getName() + "-" + comms.getPort().getName())
					.replace(' ', '-')
					.replace('/', '-');
		}

		public Bundle bundle() {
			return bundle;
		}
	}

	@interface Configuration {}

	public static final String NAME_KEY = "name";
	public static final String ID_KEY = "id";
	public static final String STATUS_KEY = "status";
	public static final String STATUS_CODE_KEY = "code";
	public static final String STATUS_FAULT_KEY = "fault";
	public static final String CHANNEL_KEY = "channel";
	public static final String BUNDLE_KEY = "registeringBundle";
	public static final String COMMANDS_KEY = "commands";

	public static final String BUNDLE_SYMBOLIC_NAME_KEY = "bundleSymbolicName";
	public static final String BUNDLE_NAME_KEY = "bundleName";
	public static final String BUNDLE_ID_KEY = "bundleId";

	public static final String COMMAND_ID_KEY = "id";
	private static final String COMMAND_OUTPUT_KEY = "output";
	private static final String ERROR_KEY = "error";
	private static final String TRACE_KEY = "trace";

	private Configuration config;

	private Map<Comms<?>, CommsRegistration> commsInterfaces;
	@SuppressWarnings("rawtypes")
	private ServiceTracker<Comms, Comms> commsInterfaceTracker;

	@Reference
	private DTOs dtos;

	@SuppressWarnings("rawtypes")
	@Activate
	void activate(Configuration config, BundleContext context) {
		modified(config);

		commsInterfaces = new LinkedHashMap<>();

		commsInterfaceTracker = new ServiceTracker<>(
				context,
				Comms.class,
				new ServiceTrackerCustomizer<Comms, Comms>() {
					@Override
					public Comms<?> addingService(ServiceReference<Comms> reference) {
						refreshCommsInterfaces(context);
						return context.getService(reference);
					}

					@Override
					public void modifiedService(ServiceReference<Comms> reference, Comms service) {
						refreshCommsInterfaces(context);
					}

					@Override
					public void removedService(ServiceReference<Comms> reference, Comms service) {
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

	@Modified
	void modified(Configuration config) {
		this.config = config;
	}

	@SuppressWarnings("rawtypes")
	private synchronized void refreshCommsInterfaces(BundleContext context) {
		commsInterfaces.clear();
		Map<Comms<?>, Bundle> commsInterfaceBundles = new HashMap<>();

		try {
			for (ServiceReference<Comms> commsReference : context
					.getServiceReferences(Comms.class, null)) {
				commsInterfaceBundles.put(context.getService(commsReference), commsReference.getBundle());
			}
		} catch (InvalidSyntaxException e) {
			throw new AssertionError();
		}

		for (Comms<?> comms : commsInterfaceBundles.keySet()) {
			CommsRegistration registration = new CommsRegistration(
					comms,
					commsInterfaceBundles.get(comms));
			commsInterfaces.put(comms, registration);
		}
	}

	public List<String> getCommsInterfaces() {
		return commsInterfaces.values().stream().map(CommsRegistration::uid).collect(toList());
	}

	public List<Map<String, Object>> getCommsInterfaceInfo() {
		return commsInterfaces.values().stream().map(this::getCommsInterfaceInfoImpl).collect(toList());
	}

	public Map<String, Object> getCommsInterfaceInfo(String name) {
		return getCommsInterfaceInfoImpl(getNamedComms(name));
	}

	public Map<String, String> postOpenCommsInterface(String name) {
		Comms<?> comms = getNamedComms(name).comms();
		comms.open();
		Map<String, String> map = new HashMap<>();
		map.put(STATUS_CODE_KEY, comms.status().get().toString());
		return map;
	}

	public Map<String, String> postResetCommsInterface(String name) {
		Comms<?> comms = getNamedComms(name).comms();
		comms.reset();
		Map<String, String> map = new HashMap<>();
		map.put(STATUS_CODE_KEY, comms.status().get().toString());
		return map;
	}

	private <T> Map<String, Object> getCommsInterfaceInfoImpl(CommsRegistration commsRegistration) {
		Map<String, Object> info = new HashMap<>();

		info.put(NAME_KEY, commsRegistration.comms().getName());
		info.put(ID_KEY, commsRegistration.uid());
		info.put(STATUS_KEY, getStatus(commsRegistration.comms()));
		info.put(CHANNEL_KEY, commsRegistration.comms().getPort().toString());
		info.put(BUNDLE_KEY, getBundleInfoImpl(commsRegistration.bundle()));
		info.put(
				COMMANDS_KEY,
				commsRegistration.comms().getCommands().map(Objects::toString).collect(toList()));

		return info;
	}

	private Map<String, Object> getStatus(Comms<?> comms) {
		Map<String, Object> info = new HashMap<>();

		info.put(STATUS_CODE_KEY, comms.status().get());
		comms.fault().ifPresent(fault -> info.put(STATUS_FAULT_KEY, fault));

		return info;
	}

	private Object getBundleInfoImpl(Bundle bundle) {
		Map<String, Object> info = new HashMap<>();

		info.put(BUNDLE_NAME_KEY, bundle.getHeaders().get(Constants.BUNDLE_NAME));
		info.put(BUNDLE_SYMBOLIC_NAME_KEY, bundle.getSymbolicName());
		info.put(BUNDLE_ID_KEY, bundle.getBundleId());

		return info;
	}

	public List<String> getCommands(String name) {
		return getNamedComms(name).comms().getCommands().map(Objects::toString).collect(toList());
	}

	public Map<String, Map<String, Object>> getCommandInfo(String commsName) {
		return getCommandInfoImpl(getNamedComms(commsName).comms());
	}

	private <T> Map<String, Map<String, Object>> getCommandInfoImpl(Comms<T> comms) {
		return comms.getCommands().collect(
				toMap(Objects::toString, c -> getCommandInfoImpl(comms.getCommand(c))));
	}

	public Map<String, Object> getCommandInfo(String commsName, String commandName) {
		Comms<?> comms = getNamedComms(commsName).comms();

		return getCommandInfoImpl(getCommand(comms, commandName));
	}

	private Command<?, ?, ?> getCommand(String commsName, String commandName) {
		Comms<?> comms = getNamedComms(commsName).comms();

		return getCommand(comms, commandName);
	}

	private <T> Command<T, ?, ?> getCommand(Comms<T> comms, String commandName) {
		return comms.getCommand(
				comms.getCommands().filter(c -> c.toString().equals(commandName)).findAny().orElseThrow(
						() -> commandNotFound(comms.getName(), commandName)));
	}

	private RuntimeException commandNotFound(String commsName, String commandName) {
		return new CommsException("Command not found " + commsName + ": " + commandName);
	}

	private Map<String, Object> getCommandInfoImpl(Command<?, ?, ?> command) {
		Map<String, Object> info = new HashMap<>();

		info.put(COMMAND_ID_KEY, command.getId().toString());
		info.put(COMMAND_OUTPUT_KEY, convertInput(command.prototype()));

		return info;
	}

	private CommsRegistration getNamedComms(String name) {
		return commsInterfaces
				.values()
				.stream()
				.filter(c -> c.uid().equals(name))
				.findAny()
				.orElseThrow(() -> new CommsException("Comms interface not found " + name));
	}

	public Map<String, ? extends Object> postCommandInvocation(
			Map<String, Object> output,
			String commsName,
			String commandName) {
		Command<?, ?, ?> command = getCommand(commsName, commandName);

		try {
			return invokeCommand(command, output);
		} catch (Exception e) {
			Map<String, String> errorMap = new HashMap<>();

			String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
			errorMap.put(ERROR_KEY, message);

			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			errorMap.put(TRACE_KEY, writer.toString());

			return errorMap;
		}
	}

	private <I, O> Map<String, Object> invokeCommand(
			Command<?, I, O> command,
			Map<String, Object> output) {
		System.out.println(convertOutput(command.prototype(), output) + " ->");

		O argument = convertOutput(command.prototype(), output);

		I result = command.invoke(argument);

		System.out.println("<- " + convertInput(result));

		return convertInput(result);
	}

	@SuppressWarnings("unchecked")
	private <O> O convertOutput(O prototype, Map<String, Object> output) {
		if (output.isEmpty())
			return null;

		O convertedOutput;

		try {
			convertedOutput = (O) dtos.convert(output).to(prototype.getClass());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommsException("Cannot convert to DTO", e);
		}

		return convertedOutput;
	}

	private <I> Map<String, Object> convertInput(I input) {
		if (input == null)
			return new HashMap<>();

		Map<String, Object> convertedInput;

		try {
			convertedInput = dtos.asMap(input);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommsException("Cannot convert from DTO", e);
		}

		return convertedInput;
	}
}
