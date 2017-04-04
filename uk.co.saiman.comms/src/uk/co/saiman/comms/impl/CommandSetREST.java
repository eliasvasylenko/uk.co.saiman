package uk.co.saiman.comms.impl;

import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

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

import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RequireRestImplementation;
import uk.co.saiman.comms.Command;
import uk.co.saiman.comms.CommandSet;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.rest.CommandRESTConverter;

@RequireRestImplementation
@Component(property = REST.ENDPOINT + "=/comms/*")
public class CommandSetREST implements REST {
	private class CommandSetRegistration {
		private final CommandSet<?> commandSet;
		private final String uid;
		private final Bundle bundle;

		public CommandSetRegistration(CommandSet<?> commandSet, Bundle bundle, String uid) {
			this.commandSet = commandSet;
			this.uid = uid;
			this.bundle = bundle;
		}

		public CommandSet<?> commandSet() {
			return commandSet;
		}

		public String uid() {
			return uid;
		}

		public Bundle bundle() {
			return bundle;
		}
	}

	@interface Configuration {}

	public static final String NAME_KEY = "name";
	public static final String ID_KEY = "id";
	public static final String COMMAND_ID_CLASS_KEY = "commandIdClass";
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
	private static final String COMMAND_INPUT_KEY = "input";
	private static final String COMMAND_OUTPUT_KEY = "output";
	private static final String ERROR_KEY = "error";
	private static final String TRACE_KEY = "trace";

	private static final String VALUE_KEY = "value";

	private Configuration config;

	private Map<CommandSet<?>, CommandSetRegistration> commandSets;
	private Map<String, CommandSetRegistration> commandSetIds;
	@SuppressWarnings("rawtypes")
	private ServiceTracker<CommandSet, CommandSet> commandSetTracker;

	@Reference(cardinality = MULTIPLE, policyOption = GREEDY)
	private List<CommandRESTConverter> converters = new CopyOnWriteArrayList<>();

	@SuppressWarnings("rawtypes")
	@Activate
	void activate(Configuration config, BundleContext context) {
		modified(config);

		commandSets = new LinkedHashMap<>();
		commandSetIds = new HashMap<>();

		commandSetTracker = new ServiceTracker<>(
				context,
				CommandSet.class,
				new ServiceTrackerCustomizer<CommandSet, CommandSet>() {
					@Override
					public CommandSet<?> addingService(ServiceReference<CommandSet> reference) {
						refreshCommandSets(context);
						return context.getService(reference);
					}

					@Override
					public void modifiedService(ServiceReference<CommandSet> reference, CommandSet service) {
						refreshCommandSets(context);
					}

					@Override
					public void removedService(ServiceReference<CommandSet> reference, CommandSet service) {
						refreshCommandSets(context);
					}
				});
		commandSetTracker.open();

		refreshCommandSets(context);
	}

	@Deactivate
	void deactivate() {
		commandSetTracker.close();
	}

	@Modified
	void modified(Configuration config) {
		this.config = config;
	}

	@SuppressWarnings("rawtypes")
	private synchronized void refreshCommandSets(BundleContext context) {
		commandSets.clear();
		Map<CommandSet<?>, Bundle> commandSetBundles = new HashMap<>();

		try {
			for (ServiceReference<CommandSet> commandSetReference : context
					.getServiceReferences(CommandSet.class, null)) {
				commandSetBundles
						.put(context.getService(commandSetReference), commandSetReference.getBundle());
			}
		} catch (InvalidSyntaxException e) {
			throw new AssertionError();
		}

		commandSetIds.values().retainAll(commandSetBundles.keySet());

		for (CommandSet<?> commandSet : commandSetBundles.keySet()) {
			String uid = commandSet.getName().replace(' ', '-');
			int i = 1;
			while (commandSetIds.containsKey(uid)) {
				uid = commandSet.getName().replace(' ', '-') + "-" + ++i;
			}

			CommandSetRegistration registration = new CommandSetRegistration(
					commandSet,
					commandSetBundles.get(commandSet),
					uid);
			commandSetIds.put(uid, registration);
			commandSets.put(commandSet, registration);
		}
	}

	public List<String> getCommandSets() {
		return commandSets.values().stream().map(CommandSetRegistration::uid).collect(toList());
	}

	public List<Map<String, Object>> getCommandSetInfo() {
		return commandSets.values().stream().map(this::getCommandSetInfoImpl).collect(toList());
	}

	public Map<String, Object> getCommandSetInfo(String name) {
		return getCommandSetInfoImpl(getNamedCommandSet(name));
	}

	private <T> Map<String, Object> getCommandSetInfoImpl(CommandSetRegistration commandSet) {
		Map<String, Object> info = new HashMap<>();

		info.put(NAME_KEY, commandSet.commandSet().getName());
		info.put(ID_KEY, commandSet.uid());
		info.put(COMMAND_ID_CLASS_KEY, commandSet.commandSet().getCommandIdClass().toString());
		info.put(STATUS_KEY, getStatus(commandSet.commandSet()));
		info.put(CHANNEL_KEY, commandSet.commandSet().getChannel().getDescriptiveName());
		info.put(BUNDLE_KEY, getBundleInfoImpl(commandSet.bundle()));
		info.put(
				COMMANDS_KEY,
				commandSet.commandSet().getCommands().map(Objects::toString).collect(toList()));

		return info;
	}

	private Map<String, Object> getStatus(CommandSet<?> commandSet) {
		Map<String, Object> info = new HashMap<>();

		info.put(STATUS_CODE_KEY, commandSet.getChannel().status().get());
		commandSet.getChannel().getFault().ifPresent(fault -> info.put(STATUS_FAULT_KEY, fault));

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
		return getNamedCommandSet(name)
				.commandSet()
				.getCommands()
				.map(Objects::toString)
				.collect(toList());
	}

	public Map<String, Map<String, Object>> getCommandInfo(String commandSetName) {
		return getCommandInfoImpl(getNamedCommandSet(commandSetName).commandSet());
	}

	private <T> Map<String, Map<String, Object>> getCommandInfoImpl(CommandSet<T> commandSet) {
		return commandSet.getCommands().collect(
				toMap(Objects::toString, c -> getCommandInfoImpl(commandSet.getCommand(c))));
	}

	public Map<String, Object> getCommandInfo(String commandSetName, String commandName) {
		CommandSet<?> commandSet = getNamedCommandSet(commandSetName).commandSet();

		return getCommandInfoImpl(getCommand(commandSet, commandName));
	}

	private Command<?, ?, ?> getCommand(String commandSetName, String commandName) {
		CommandSet<?> commandSet = getNamedCommandSet(commandSetName).commandSet();

		return getCommand(commandSet, commandName);
	}

	private <T> Command<T, ?, ?> getCommand(CommandSet<T> commandSet, String commandName) {
		return commandSet.getCommand(
				commandSet
						.getCommands()
						.filter(c -> c.toString().equals(commandName))
						.findAny()
						.orElseThrow(() -> commandNotFound(commandSet.getName(), commandName)));
	}

	private RuntimeException commandNotFound(String commandSetName, String commandName) {
		return new CommsException("Command not found " + commandSetName + ": " + commandName);
	}

	private Map<String, Object> getCommandInfoImpl(Command<?, ?, ?> command) {
		Map<String, Object> info = new HashMap<>();

		info.put(COMMAND_ID_KEY, command.getId().toString());
		info.put(COMMAND_OUTPUT_KEY, convertInput(command.prototype()));

		return info;
	}

	private CommandSetRegistration getNamedCommandSet(String name) {
		return ofNullable(commandSetIds.get(name))
				.orElseThrow(() -> new CommsException("Command set not found " + name));
	}

	public Map<String, ? extends Object> putCommandInvocation(
			Map<String, Object> output,
			String commandSetName,
			String commandName) {
		Command<?, ?, ?> command = getCommand(commandSetName, commandName);

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
		O argument = convertOutput(command.prototype(), output);

		I result = command.invoke(argument);

		return convertInput(result);
	}

	@SuppressWarnings("unchecked")
	private <O> O convertOutput(O prototype, Map<String, Object> output) {
		if (output.isEmpty())
			return null;

		O convertedOutput = null;

		for (CommandRESTConverter converter : converters) {
			convertedOutput = (O) converter.convertOutput(prototype, output);

			if (convertedOutput != null)
				break;
		}

		if (convertedOutput == null) {
			convertedOutput = (O) output.get(VALUE_KEY);
		}

		return convertedOutput;
	}

	private <I> Map<String, Object> convertInput(I input) {
		if (input == null)
			return new HashMap<>();

		Map<String, Object> convertedInput = null;

		for (CommandRESTConverter converter : converters) {
			Map<String, ?> converted = converter.convertInput(input);

			if (converted != null) {
				convertedInput = unmodifiableMap(converted);
				break;
			}
		}

		if (convertedInput == null) {
			convertedInput = new HashMap<>();
			convertedInput.put(VALUE_KEY, input);
		}

		return convertedInput;
	}
}
